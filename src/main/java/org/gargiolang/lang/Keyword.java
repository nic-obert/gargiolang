package org.gargiolang.lang;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.lang.operators.ArithmeticOperator;
import org.gargiolang.lang.operators.Parenthesis;
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

    IF("if", (byte) 9),
    ELSE("else", (byte) 9),

    WHILE("while", (byte) 10),
    FOR("for", (byte) 10),

    DEF("def", (byte) 10),

    F("f", (byte) 10); // like python's f-strings f"my name is {name}"

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
        Keyword keyword = (Keyword) interpreter.getLine().get(interpreter.getCurrentTokenIndex()).getValue();

        switch (keyword)
        {
            case BREAK -> {
                /*
                    - clear the current line
                    - create a token of type KEYWORD and value of BREAK
                    - set its priority to 0
                    - add it to the current line
                 */

                interpreter.getLine().clear();
                Token token = new Token(Token.TokenType.KEYWORD, Keyword.BREAK);
                token.setPriority(0);
                interpreter.getLine().add(token);
            }

            case CONTINUE -> {
                /*
                    - clear the current line
                    - create a token of type KEYWORD and value of CONTINUE
                    - set its priority to 0
                    - add it to the current line
                 */

                interpreter.getLine().clear();
                Token token = new Token(Token.TokenType.KEYWORD, Keyword.CONTINUE);
                token.setPriority(0);
                interpreter.getLine().add(token);
            }

            case IF -> {
                LinkedList<Token> line = interpreter.getLine();
                Token condition = line.get(interpreter.getCurrentTokenIndex() + 1);

                // check if condition is actually a boolean
                if (!condition.getVarType(interpreter.getRuntime()).equals(Variable.Type.BOOLEAN))
                    throw new BadTypeException("Condition is not a boolean: " + condition);

                // remove if keyword and condition
                line.remove(condition);
                line.remove(interpreter.getCurrentTokenIndex());

                // get the position of the next matching scopes
                int[][] scopes = Scope.findNextScope(interpreter);

                // check if the boolean condition is true
                if (condition.getVarValue(interpreter.getRuntime()).equals(true)) {
                    interpreter.setLineFrom(scopes[0][0], scopes[0][1]);
                    interpreter.setCurrentTokenIndex(0);
                    // tell the interpreter not to search for the highest priority token next time
                    interpreter.blockCurrentTokenIndex();
                }
                // whereas if the boolean condition is false
                else {
                    interpreter.setLineFrom(scopes[1][0], scopes[1][1] + 1);
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
                int[][] parenthesis = Parenthesis.findNextParenthesis(interpreter);

                // get the position of the for loop's block
                int[][] forBlock = Scope.findNextScope(interpreter);
                /*
                    forBlock[][]'s structure:
                        [0][0] --> line of the opening scope
                        [0][1] --> index of the opening scope in its line
                        [1][0] --> line of the closing scope
                        [1][1] --> index of the closing scope in its line
                 */

                // store statement positions for ease of use
                int secondStatementLine = parenthesis[0][0] + 1;
                int thirdStatementLine = parenthesis[0][0] + 2;

                // execute the first statement (e.g. int i = 0;)
                interpreter.setLineFrom(parenthesis[0][0], parenthesis[0][1] + 1);
                interpreter.executeLine();

                // check if the second statement evaluates to a boolean (e.g. i < 10;)
                interpreter.setLine(secondStatementLine);
                interpreter.executeLine();
                if (interpreter.getLine().size() == 0 || !interpreter.getLine().getFirst().getType().equals(Token.TokenType.BOOL))
                    throw new BadTypeException("Statement does not evaluate to a boolean: " + interpreter.getLine(secondStatementLine));


                /*
                    - check the boolean statement
                    - execute the code block
                    - execute the third statement
                 */
                boolean forLooping = true;
                while (forLooping) {

                    // check the second boolean statement (the loop's condition e.g. i < 10;)
                    interpreter.setLine(secondStatementLine);
                    if (interpreter.executeLine().getFirst().getVarValue(interpreter.getRuntime()).equals(false)) break;

                    // execute the code block

                    // set the line execution to the beginning of the for loop
                    interpreter.setLineFrom(forBlock[0][0], forBlock[0][1]);
                    boolean executingBlock = true;
                    while (executingBlock) {

                        // execute the current line and store result
                        interpreter.executeLine();

                        // check first if line is not empty
                        if (interpreter.getLine().size() != 0) {
                            // get the result of line execution
                            Token result = interpreter.getLine().getFirst();

                            // if the result is a BREAK or CONTINUE keyword --> act consequently
                            if (result.getType().equals(Token.TokenType.KEYWORD)) {
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
                        if (interpreter.getLineIndex() == forBlock[1][0] - 1) {
                            interpreter.setLineUntil(forBlock[1][0], forBlock[1][1] + 1);
                        }
                        // if the current line is the last line of the loop --> set executingBlock to false to break the loop
                        else if (interpreter.getLineIndex() == forBlock[1][0]) {
                            executingBlock = false;
                        }
                        // otherwise just jump to the next line
                        else {
                            interpreter.setLine(interpreter.getLineIndex() + 1);
                        }

                    } // ---------------------------- end of code block

                    // execute third statement (e.g. i++;)
                    interpreter.setLineUntil(thirdStatementLine, parenthesis[1][1]);
                    interpreter.executeLine();

                } // ----------------------------------  end of for loop


                // pop previously added scopes
                interpreter.getRuntime().getSymbolTable().popScopes(interpreter.getRuntime().getSymbolTable().scopeCount() - scopeCount);

                // set the line execution to after the for loop
                interpreter.setLineFrom(forBlock[1][0], forBlock[1][1] + 1);

            }

            case ELSE -> {
                // jump to the end of the scope (do not execute this block of code)
                int[][] scopes = Scope.findNextScope(interpreter);
                interpreter.setLineFrom(scopes[1][0], scopes[1][1] + 1);
            }

            case GOTO -> {
                // get the next token in line
                Token toLineToken = interpreter.getLine().get(interpreter.getCurrentTokenIndex() + 1);
                // check if the label to go to is of type Token.TokenType.TXT
                if (toLineToken.getType().equals(Token.TokenType.TXT)) {
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
                interpreter.getLine().remove(interpreter.getCurrentTokenIndex());
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
                int[][] parenthesis = Parenthesis.findNextParenthesis(interpreter);

                // get the code block
                int[][] whileBlock = Scope.findNextScope(interpreter);

                // check if loop's condition statement evaluates to a boolean
                interpreter.setLineBetween(parenthesis[0][0], parenthesis[0][1] + 1, parenthesis[1][1]);
                interpreter.executeLine();
                if (interpreter.getLine().size() == 0 || !interpreter.getLine().getFirst().getType().equals(Token.TokenType.BOOL))
                    throw new BadTypeException("Statement does not evaluate to a boolean: " + interpreter.getLine(parenthesis[0][0]).subList(parenthesis[0][1], parenthesis[1][1]));


                boolean whileLooping = true;
                while (whileLooping) {

                    // check the loop's condition
                    interpreter.setLineBetween(parenthesis[0][0], parenthesis[0][1] + 1, parenthesis[1][1]);
                    if (interpreter.executeLine().getFirst().getValue().equals(false)) break;

                    // execute the code block

                    // set the line execution to the beginning of the code block
                    interpreter.setLineFrom(whileBlock[0][0], whileBlock[0][1]);
                    boolean executingBlock = true;
                    while (executingBlock) {

                        // execute the current line
                        interpreter.executeLine();

                        // check first if the line is not empty
                        if (interpreter.getLine().size() != 0) {
                            // get the result of line execution
                            Token result = interpreter.getLine().getFirst();

                            // if the result is a BREAK or CONTINUE keyword --> act consequently
                            if (result.getType().equals(Token.TokenType.KEYWORD)) {
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
                        if (interpreter.getLineIndex() == whileBlock[1][0] - 1) {
                            interpreter.setLineUntil(whileBlock[1][0], whileBlock[1][1] + 1);
                        }
                        // if the current line is the last line of the loop --> set executingBlock to false to break the loop
                        else if (interpreter.getLineIndex() == whileBlock[1][0]) {
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
                interpreter.setLineFrom(whileBlock[1][0], whileBlock[1][1] + 1);
            }

            case GOBACK -> {
                Stack<Integer> gotoStack = interpreter.getRuntime().getGotoStack();
                if (gotoStack.isEmpty())
                    throw new GoBackException("Cannot go back more than this: goto stack is empty");
                interpreter.setLineIndex(gotoStack.pop() - 1);

                // finally remove the keyword
                interpreter.getLine().remove(interpreter.getCurrentTokenIndex());
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


                LinkedList<Token> line = interpreter.getLine();
                int currentTokenIndex = interpreter.getCurrentTokenIndex();


                // get return type
                Token returnTypeToken = line.get(currentTokenIndex + 1);
                if (!returnTypeToken.getType().equals(Token.TokenType.TYPE))
                    throw new UnrecognizedTypeException("Unrecognized return type: " + returnTypeToken);
                Variable.Type returnType = (Variable.Type) returnTypeToken.getValue();


                // get function name
                Token functionNameToken = line.get(currentTokenIndex + 2);
                if (!functionNameToken.getType().equals(Token.TokenType.FUNC))
                    throw new BadTypeException("Token.TokenType.FUNC expected for function names, but " + functionNameToken + " was provided instead");
                String functionName = (String) functionNameToken.getValue();


                // get parameter list
                LinkedList<Parameter> params = new LinkedList<>();

                int paramIndex = currentTokenIndex + 4;

                while (!line.get(paramIndex).getType().equals(Token.TokenType.CALL)) {

                    // get argument type
                    Token paramType = line.get(paramIndex);
                    if (!paramType.getType().equals(Token.TokenType.TYPE))
                        throw new UnrecognizedTypeException("Unrecognized return type: " + paramType);

                    paramIndex ++;

                    // get argument name
                    Token paramName = line.get(paramIndex);
                    if (!paramName.getType().equals(Token.TokenType.TXT))
                        throw new BadTypeException("Token.TokenType.TXT expected for parameter names, but " + paramName + " was provided instead");

                    paramIndex ++;

                    params.add(new Parameter((String) paramName.getValue(), (Variable.Type) paramType.getValue()));
                }


                // get function code block
                int[][] codeBlock = Scope.findNextScope(interpreter);


                // create the function
                Function function = new Function(
                        codeBlock[0][0],
                        codeBlock[0][1],
                        params,
                        returnType
                );


                // append the function to the FunctionTable
                interpreter.getRuntime().getFunctionTable().addFunction(functionName, function);


                // set the line execution to after the function's code block
                interpreter.setLineFrom(codeBlock[1][0], codeBlock[1][1] + 1);

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
                    Token returnToken = interpreter.getLine().get(interpreter.getCurrentTokenIndex() + 1);
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
                interpreter.setLine(call.getLineState(), call.getCalledFromLine(), call.getCalledFromIndex());


                // initialize return token
                Token returnToken;
                if (returnValue == null) {
                    returnToken = new Token(Token.TokenType.NULL, null);
                } else {
                    returnToken = new Token(Token.TokenType.fromVarType(returnValue.getType()), returnValue.getValue());
                }

                // actually return the token
                interpreter.getLine().set(interpreter.getCurrentTokenIndex(), returnToken);

            }

            case SYSTEM -> {
                /*
                    - get function name
                    - get function arguments
                    - call the function
                    - clear the line
                 */

                LinkedList<Token> line = interpreter.getLine();
                int currentTokenIndex = interpreter.getCurrentTokenIndex();
                Runtime runtime = interpreter.getRuntime();


                // get function name to call
                Token funcNameToken = line.get(currentTokenIndex + 1);
                // ensure argument is a string
                if (!funcNameToken.getVarType(runtime).equals(Variable.Type.STRING))
                    throw new BadTypeException("Type String is required, but " + funcNameToken + " was provided");
                String funcName = (String) funcNameToken.getVarValue(runtime);


                // get function arguments
                LinkedList<Object> args = new LinkedList<>();
                // every token in the line is considered to be an argument
                for (Token token : line.subList(currentTokenIndex + 2, line.size())) {
                    args.add(token.getVarValue(runtime));
                }


                // invoke the system call
                Object obj = ReflectionUtils.invokeSystemCall(funcName, args);
                Variable.Type type = funcNameToken.getVarType(runtime);

                if(type.equals(Variable.Type.NULL)) {
                    // clear the line
                    line.clear();
                } else {
                    if (line.size() > currentTokenIndex) {
                        line.subList(currentTokenIndex, line.size()).clear();
                    }

                    Token.TokenType t = null;

                    switch (type){
                        case INT, FLOAT -> t = Token.TokenType.NUM;
                        case BOOLEAN -> t = Token.TokenType.BOOL;
                        case STRING -> t = Token.TokenType.STR;
                    }

                    assert t != null;
                    line.add(new Token(t, obj));
                }

            }

            case F -> {
                /*
                    - get the string to format
                    - get variable names for formatting
                    - convert the f-string to a chained sum of strings
                 */

                LinkedList<Token> line = interpreter.getLine();
                int currentTokenIndex = interpreter.getCurrentTokenIndex();


                // get the string to format
                Token fStringToken = line.get(currentTokenIndex + 1);
                line.remove(currentTokenIndex);
                // ensure fString is actually a literal string (not a variable of type String)
                if (!fStringToken.getType().equals(Token.TokenType.STR))
                    throw new BadTypeException("Can only format Strings, but " + fStringToken + " was provided");


                // search for variable names in the string

                String fString = (String) fStringToken.getValue();
                line.remove(fStringToken);

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
                        Token strBefore = new Token(Token.TokenType.STR, fString.substring(lastBracket, i));

                        // add the string before the variable
                        line.add(currentTokenIndex, strBefore);
                        currentTokenIndex ++;

                        // insert a + operator
                        line.add(currentTokenIndex, new Token(Token.TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.ADD));
                        currentTokenIndex ++;

                        // get the variable in the string
                        String varName = fString.substring(i + 1, closingBracket);
                        Token variable = new Token(Token.TokenType.TXT, varName);

                        // insert the variable
                        line.add(currentTokenIndex, variable);
                        currentTokenIndex ++;

                        // insert a + operator
                        line.add(currentTokenIndex, new Token(Token.TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.ADD));
                        currentTokenIndex ++;

                        // set the iteration index to after the closing bracket
                        i = closingBracket;
                        lastBracket = closingBracket + 1;

                    }

                } // end of for loop

                // add the rest of the string
                line.add(currentTokenIndex, new Token(Token.TokenType.STR, fString.substring(lastBracket)));

            }
        }

    }


}
