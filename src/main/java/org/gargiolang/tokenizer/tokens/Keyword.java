package org.gargiolang.tokenizer.tokens;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.tokenizer.tokens.operators.ArithmeticOperator;
import org.gargiolang.tokenizer.tokens.operators.Parenthesis;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.function.Call;
import org.gargiolang.runtime.function.Function;
import org.gargiolang.runtime.function.Parameter;
import org.gargiolang.runtime.variable.Accessibility;
import org.gargiolang.runtime.variable.SymbolTable;
import org.gargiolang.runtime.variable.Variable;
import org.gargiolang.util.ReflectionUtils;

import java.util.LinkedList;
import java.util.Stack;

public enum Keyword {

    GOTO("goto", (byte) 1),
    GOBACK("goback", (byte) 1),

    RETURN("return", (byte) 1),

    BREAK("break", (byte) 1),
    CONTINUE("continue", (byte) 1),

    SYSTEM("system", (byte) 2),  // kind of the C system() function, but a keyword

    F("f", (byte) 9), // like python's f-strings f"my name is {name}"

    IF("if", (byte) 9),
    ELSE("else", (byte) 9),

    WHILE("while", (byte) 10),
    FOR("for", (byte) 10),

    DEF("def", (byte) 10);


    private final String value;
    private final byte priority;


    Keyword(String str, byte priority){
        this.value = str;
        this.priority = priority;
    }

    public static boolean isKeyword(String str){
        for(Keyword keyword : Keyword.values()){
            if(keyword.value.equals(str)) return true;
        }
        return false;
    }


    public int getPriority() {
        return this.priority;
    }


    public static void evaluate(Interpreter interpreter) throws EvaluationException, ReflectiveOperationException {
        Token currentToken = interpreter.getCurrentToken();

        switch ((Keyword) currentToken.getValue())
        {
            case BREAK -> {
                /*
                    - clear the current line
                    - create a token of type KEYWORD and value of BREAK
                    - set its priority to 0
                    - add it to the current line
                 */

                interpreter.getLine().clear();
                Token token = new Token(TokenType.KEYWORD, Keyword.BREAK);
                token.setPriority(0);
                interpreter.getLine().append(token);
            }

            case CONTINUE -> {
                /*
                    - clear the current line
                    - create a token of type KEYWORD and value of CONTINUE
                    - set its priority to 0
                    - add it to the current line
                 */

                interpreter.getLine().clear();
                Token token = new Token(TokenType.KEYWORD, Keyword.CONTINUE);
                token.setPriority(0);
                interpreter.getLine().append(token);
            }

            case IF -> {
                TokenLine line = interpreter.getLine();
                Token condition = currentToken.getNext();

                // check if condition is actually a boolean
                if (!condition.getVarType(interpreter.getRuntime()).equals(Variable.Type.BOOLEAN))
                    throw new BadTypeException("Condition is not a boolean: " + condition);

                // remove if keyword and condition
                line.remove(condition);
                line.remove(currentToken);

                // get the position of the next matching scopes
                TokenBlock ifBlock = Scope.findNextScope(interpreter);

                // check if the boolean condition is true
                if (condition.getVarValue(interpreter.getRuntime()).equals(true)) {
                    interpreter.setLineFrom(ifBlock.getFirstLine(), ifBlock.getFirstToken());
                    interpreter.setCurrentToken(line.getFirst());
                    // tell the interpreter not to search for the highest priority token next time
                    interpreter.blockCurrentToken();
                }
                // whereas if the boolean condition is false
                else {
                    interpreter.setLineFrom(ifBlock.getLastLine(), ifBlock.getLastToken().getNext());
                    // check if there is an else --> remove it
                    if (interpreter.getLine().getFirst().getValue().equals(ELSE)) {
                        interpreter.getLine().removeFirst();
                    }
                }
            }

            case FOR -> {
                /*
                    Algorithm:
                        - store the current number of scopes

                        - firstly push a new scope to the stack
                        - get the position of the code block to execute
                        - execute the first statement (e.g. int i = 0;)

                        - check the second boolean statement (e.g. i > 10;)
                        - execute code block in the next scope
                        - execute the third statement (e.g. i++;)
                        - repeat

                        - when the loop is broken --> pop the previously added scopes
                        - set line execution to after the while loop
                 */

                // store the current number of scopes
                int scopeCount = interpreter.getRuntime().getSymbolTable().scopeCount();

                // push a new scope to the stack
                interpreter.getRuntime().getSymbolTable().pushScope();

                // get the position of parenthesis
                TokenBlock parenthesis = Parenthesis.findNextParenthesis(interpreter);

                // get the position of the for loop's block
                TokenBlock forBlock = Scope.findNextScope(interpreter);

                // store the reference to each statement for ease of use
                TokenLine condition = interpreter.getLine(parenthesis.getFirstLine() + 1);

                TokenLine action = interpreter.getLine(parenthesis.getLastLine());
                action = action.subList(action.getFirst(), parenthesis.getLastToken().getPrev());

                // execute the first statement (e.g. int i = 0;)
                interpreter.setLineFrom(parenthesis.getFirstLine(), parenthesis.getFirstToken().getNext());
                interpreter.executeLine();

                // check if the condition statement evaluates to a boolean (e.g. i < 10;)
                interpreter.setLine(condition.copy());
                interpreter.executeLine();
                if (interpreter.getLine().isEmpty() || interpreter.getLine().getFirst().getType() != TokenType.BOOL)
                    throw new BadTypeException("Statement does not evaluate to a boolean: " + condition);


                /*
                    - check the boolean statement
                    - execute the code block
                    - execute the third statement
                 */
                boolean forLooping = true;
                while (forLooping) {

                    // check the second boolean statement (the loop's condition e.g. i < 10;)
                    interpreter.setLine(condition.copy());
                    if (!(boolean) interpreter.executeLine().getFirst().asBool().getValue())
                        break;

                    // execute the code block

                    // set the line execution to the beginning of the for loop
                    interpreter.setLineFrom(forBlock.getFirstLine(), forBlock.getFirstToken());
                    boolean executingBlock = true;
                    while (executingBlock) {

                        // execute the current line and store result
                        interpreter.executeLine();

                        // check first if line is not empty
                        if (!interpreter.getLine().isEmpty()) {
                            // get the result of line execution
                            Token result = interpreter.getLine().getFirst();

                            // if the result is a BREAK or CONTINUE keyword --> act consequently
                            if (result.getType() == TokenType.KEYWORD) {
                                switch ((Keyword) result.getValue()) {
                                    case BREAK -> {
                                        forLooping = false;
                                        executingBlock = false;
                                    }
                                    case CONTINUE -> executingBlock = false;
                                }
                            }
                        }

                        // if the next line is the last line of the loop --> include only until the closing scope
                        if (interpreter.getLineIndex() == forBlock.getLastLine() - 1) {
                            interpreter.setLineUntil(forBlock.getLastLine(), forBlock.getLastToken());
                        }
                        // if the current line is the last line of the loop --> set executingBlock to false to break the loop
                        else if (interpreter.getLineIndex() == forBlock.getLastLine()) {
                            executingBlock = false;
                        }
                        // otherwise just jump to the next line
                        else {
                            interpreter.setLine(interpreter.getLineIndex() + 1);
                        }

                    } // ---------------------------- end of code block

                    // execute third statement (e.g. i++;)
                    interpreter.setLine(action.copy());
                    interpreter.executeLine();

                } // ----------------------------------  end of for loop


                // pop previously added scopes
                interpreter.getRuntime().getSymbolTable().popScopes(interpreter.getRuntime().getSymbolTable().scopeCount() - scopeCount);

                // set the line execution to after the for loop
                interpreter.setLineFrom(forBlock.getLastLine(), forBlock.getLastToken().getNext());

            }

            case ELSE -> {
                // jump to the end of the scope (do not execute this block of code)
                TokenBlock scopes = Scope.findNextScope(interpreter);
                interpreter.setLineFrom(scopes.getLastLine(), scopes.getLastToken().getNext());
            }

            case GOTO -> {
                // get the next token in line
                Token toLineToken = currentToken.getNext();
                // check if the label to go to is of type TokenType.TXT
                if (toLineToken.getType() == TokenType.TXT) {
                    // push the lineIndex from where goto has been called to the gotoStack
                    interpreter.getRuntime().getGotoStack().push(interpreter.getLineIndex());
                    // set the line of execution to the specified labelled line
                    interpreter.setLineIndex(
                            interpreter.getRuntime().getLabelTable().getLabel((String) toLineToken.getValue()) - 1); // -1 because the interpreter increments lineIndex by 1 in its for loop
                } else {
                    throw new BadTypeException("Goto accepts only TokenType.TXT, but '" + toLineToken.getType() + "' was provided");
                }

                // finally remove the goto keyword and the label
                interpreter.getLine().remove(toLineToken);
                interpreter.getLine().remove(currentToken);
            }

            case WHILE -> {
                /*
                    - store the current number of scopes

                    - first push a new scope to the stack
                    - check the boolean condition
                    - execute the code block

                    - pop the previously added scope
                    - set line execution to after the while loop
                 */

                // store the current number of scopes
                int scopeCount = interpreter.getRuntime().getSymbolTable().scopeCount();

                // push new scope
                interpreter.getRuntime().getSymbolTable().pushScope();

                // get the loop's condition statement
                TokenBlock parenthesis = Parenthesis.findNextParenthesis(interpreter);
                TokenLine condition = interpreter.getLine(parenthesis.getFirstLine()).subList(parenthesis.getFirstToken().getNext(), parenthesis.getLastToken().getPrev());

                // get the code block
                TokenBlock whileBlock = Scope.findNextScope(interpreter);

                // check if loop's condition statement evaluates to a boolean
                interpreter.setLine(condition.copy());
                interpreter.executeLine();
                if (interpreter.getLine().isEmpty() || interpreter.getLine().getFirst().getType() != TokenType.BOOL)
                    throw new BadTypeException("Statement does not evaluate to a boolean: " + interpreter.getLine(parenthesis.getFirstLine()).subList(parenthesis.getFirstToken().getNext(), parenthesis.getLastToken()));


                boolean whileLooping = true;
                while (whileLooping) {

                    // check the loop's condition
                    interpreter.setLine(condition.copy());
                    if (!(boolean)interpreter.executeLine().getFirst().asBool().getValue())
                        break;

                    // execute the code block

                    // set the line execution to the beginning of the code block
                    interpreter.setLineFrom(whileBlock.getFirstLine(), whileBlock.getFirstToken());
                    boolean executingBlock = true;
                    while (executingBlock) {

                        // execute the current line
                        interpreter.executeLine();

                        // check first if the line is not empty
                        if (!interpreter.getLine().isEmpty()) {
                            // get the result of line execution
                            Token result = interpreter.getLine().getFirst();

                            // if the result is a BREAK or CONTINUE keyword --> act consequently
                            if (result.getType() == TokenType.KEYWORD) {
                                switch ((Keyword) result.getValue()) {
                                    case BREAK -> {
                                        whileLooping = false;
                                        executingBlock = false;
                                    }
                                    case CONTINUE -> executingBlock = false;
                                }
                            }
                        }

                        // if the next line is the last line of the loop --> include only until the closing scope
                        if (interpreter.getLineIndex() == whileBlock.getLastLine() - 1) {
                            interpreter.setLineUntil(whileBlock.getLastLine(), whileBlock.getLastToken());
                        }
                        // if the current line is the last line of the loop --> set executingBlock to false to break the loop
                        else if (interpreter.getLineIndex() == whileBlock.getLastLine()) {
                            executingBlock = false;
                        }
                        // otherwise just jump to the next line
                        else {
                            interpreter.setLine(interpreter.getLineIndex() + 1);
                        }

                    } // --------------------- end of code block

                } // -------------------------- end of while loop

                // pop previously added scopes
                interpreter.getRuntime().getSymbolTable().popScopes(interpreter.getRuntime().getSymbolTable().scopeCount() - scopeCount);

                // set line execution to after the while loop
                interpreter.setLineFrom(whileBlock.getLastLine(), whileBlock.getLastToken().getNext());
            }

            case GOBACK -> {
                Stack<Integer> gotoStack = interpreter.getRuntime().getGotoStack();
                if (gotoStack.isEmpty())
                    throw new GoBackException("Cannot go back more than this: goto stack is empty");
                interpreter.setLineIndex(gotoStack.pop() - 1);

                // finally remove the keyword
                interpreter.getLine().remove(currentToken);
            }

            case DEF -> {
                /*
                    - first get return type
                    - get function name
                    - get function parameters
                    - get function body
                    - add the new function to the FunctionTable
                    - set line execution to after the function's code block
                 */


                // get return type
                Token returnTypeToken = currentToken.getNext();
                if (!returnTypeToken.getType().equals(TokenType.TYPE))
                    throw new UnrecognizedTypeException("Unrecognized return type: " + returnTypeToken);
                Variable.Type returnType = (Variable.Type) returnTypeToken.getValue();


                // get function name
                Token functionNameToken = returnTypeToken.getNext();
                if (!functionNameToken.getType().equals(TokenType.FUNC))
                    throw new BadTypeException("TokenType.FUNC expected for function names, but " + functionNameToken + " was provided instead");
                String functionName = (String) functionNameToken.getValue();


                // get parameter list
                LinkedList<Parameter> params = new LinkedList<>();


                for (Token paramToken = functionNameToken.getNext().getNext(); !paramToken.getType().equals(TokenType.CALL); ) {

                    // get argument type
                    if (!paramToken.getType().equals(TokenType.TYPE))
                        throw new UnrecognizedTypeException("Unrecognized return type: " + paramToken);
                    Variable.Type paramType = (Variable.Type) paramToken.getValue();

                    paramToken = paramToken.getNext();

                    // get argument name
                    if (!paramToken.getType().equals(TokenType.TXT))
                        throw new BadTypeException("TokenType.TXT expected for parameter names, but " + paramToken + " was provided instead");
                    String paramName = (String) paramToken.getValue();

                    paramToken = paramToken.getNext();

                    params.add(new Parameter(paramName, paramType));
                }


                // get function code block
                TokenBlock tokenBlock = Scope.findNextScope(interpreter);

                // create the function
                Function function = new Function(
                        tokenBlock.getFirstLine(),
                        tokenBlock.getFirstToken(),
                        params,
                        returnType
                );


                // append the function to the FunctionTable
                interpreter.getRuntime().getFunctionTable().addFunction(functionName, function);


                // set the line execution to after the function's code block
                interpreter.setLineFrom(tokenBlock.getLastLine(), tokenBlock.getLastToken().getNext());

            }

            case RETURN -> {

                /*
                    - pop Call from the CallStack
                    - get the return value (if any)
                    - pop ALL the previously added scopes
                    - get back to where the function was called
                    - return the function's return value
                 */

                Runtime runtime = interpreter.getRuntime();
                SymbolTable symbolTable = runtime.getSymbolTable();


                // pop Call from the CallStack
                Call call = runtime.getCallStack().pop();
                Function function = call.getFunction();


                // get the return value (if any)
                Variable returnValue = null;
                if (!function.getReturnType().equals(Variable.Type.NULL)) {
                    Token returnToken = currentToken.getNext();
                    Variable.Type returnType = returnToken.getVarType(runtime);
                    // check if return types match
                    if (!returnType.equals(function.getReturnType()))
                        throw new BadTypeException("Return type is '" + function.getReturnType() + "', but '" + returnType + "' was provided");

                    returnValue = new Variable(
                            returnToken.getVarValue(runtime),
                            returnType,
                            Accessibility.PUBLIC
                    );
                }


                // pop ALL previously added scopes
                symbolTable.popScopes(symbolTable.scopeCount() - call.getScopeCount());


                // restore the previous interpreter's state
                interpreter.setLine(call.getLineState(), call.getCalledFromLine(), call.getCalledFromToken());


                // initialize return token
                Token returnToken;
                if (returnValue == null) {
                    returnToken = new Token(TokenType.NULL, null);
                } else {
                    returnToken = new Token(TokenType.fromVarType(returnValue.getType()), returnValue.getValue());
                }

                // actually return the token
                interpreter.getLine().replace(currentToken, returnToken);

            }

            case SYSTEM -> {
                /*
                    - get function name
                    - get function arguments
                    - call the function
                    - clear the line
                 */

                TokenLine line = interpreter.getLine();
                Runtime runtime = interpreter.getRuntime();


                // get function name to call
                Token funcNameToken = currentToken.getNext();
                // ensure argument is a string
                if (funcNameToken.getVarType(runtime) != Variable.Type.STRING)
                    throw new BadTypeException("Type String is required, but " + funcNameToken + " was provided");
                String funcName = (String) funcNameToken.getVarValue(runtime);


                // get function arguments
                LinkedList<Object> args = new LinkedList<>();
                // every token in the line is considered to be an argument
                for (Token token = funcNameToken.getNext(); token != null; token = token.getNext()) {
                    args.add(token.getVarValue(runtime));
                }


                // invoke the system call
                Object obj = ReflectionUtils.invokeSystemCall(funcName, args);
                Variable.Type type = funcNameToken.getVarType(runtime);

                if(type == Variable.Type.NULL) {
                    // clear the line
                    line.clear();
                } else {
                    // TODO: 03/12/20 what is this supposed to do?
                    if (line.size() > line.indexOf(currentToken)) {
                        line.removeFrom(currentToken);
                    }

                    TokenType tokenType = null;

                    switch (type){
                        case INT, FLOAT -> tokenType = TokenType.NUM;
                        case BOOLEAN -> tokenType = TokenType.BOOL;
                        case STRING -> tokenType = TokenType.STR;
                    }

                    assert tokenType != null;
                    line.append(new Token(tokenType, obj));
                }

            }

            case F -> {
                /*
                    - get the string to format
                    - get variable names for formatting
                    - convert the f-string to a chained sum of strings
                 */

                TokenLine line = interpreter.getLine();


                // get the string to format
                Token fStringToken = currentToken.getNext();
                // ensure fString is actually a literal string (not a variable of type String or any other token)
                if (fStringToken.getType() != TokenType.STR)
                    throw new BadTypeException("Can only format Strings, but " + fStringToken + " was provided");


                // search for variable names in the string

                String fString = (String) fStringToken.getValue();
                // currentToken reference is changed --> from here it doesn't point to the current token that is being evaluated (namely "f")
                line.remove(currentToken);
                currentToken = fStringToken;

                char[] charArray = fString.toCharArray();
                int lastBracket = 0;

                boolean escape = false;
                for (int i = 0; i != charArray.length; i++) {
                    char c = charArray[i];

                    if (c == '\\') escape = !escape;

                    else if (escape && c == '{')
                        continue;

                    if (c == '{') {

                        // get the index of closing bracket
                        int closingBracket = fString.indexOf("}", i);
                        // check there is a closing '}'
                        if (closingBracket == -1)
                            throw new OpenBracketException("Bracket in f-string is open, but never closed: '" + fString + "'");

                        // get the string before the variable
                        Token strBefore = new Token(TokenType.STR, fString.substring(lastBracket, i));

                        // add the string before the variable
                        line.insertAfter(currentToken, strBefore);

                        // insert a + operator
                        line.insertAfter(strBefore, new Token(TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.ADD));

                        // get the variable in the string
                        String varName = fString.substring(i + 1, closingBracket);
                        Token variable = new Token(TokenType.TXT, varName);

                        // insert the variable
                        line.insertAfter(strBefore.getNext(), variable);

                        // insert a + operator
                        line.insertAfter(variable, new Token(TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.ADD));
                        currentToken = variable.getNext();

                        // set the iteration index to after the closing bracket
                        i = closingBracket;
                        lastBracket = closingBracket + 1;

                    }

                } // end of for loop

                // add the rest of the string
                line.insertAfter(currentToken, new Token(TokenType.STR, fString.substring(lastBracket)));

                // remove the old string
                line.remove(fStringToken);
            }
        }

    }


}
