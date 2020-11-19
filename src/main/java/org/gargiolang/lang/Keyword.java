package org.gargiolang.lang;

public enum Keyword {

    IF("if"),
    ELSE("else"),

    GOTO("goto");

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
