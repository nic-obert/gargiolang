package org.gargiolang.lang;

public enum Keyword {

    STRING("string"),
    INT("int"),
    FLOAT("float"),
    NULL("null"),

    DEF("def"),

    IF("if"),
    ELSE("else");

    private final String value;

    Keyword(String str){
        this.value = str;
    }

    public static boolean isKeyword(String str){
        for(Keyword keyword : Keyword.values()){
            if(keyword.value.equals(str)) return true;
        }
        return false;
    }

}
