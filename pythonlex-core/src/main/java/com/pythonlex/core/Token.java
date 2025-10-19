package com.pythonlex.core;
public class Token {
    public final TokenType type;
    public final String lexeme;
    public final int line;
    public final int column;
    public final int length;
    public Token(TokenType type, String lexeme, int line, int column, int length) {
        this.type = type; this.lexeme = lexeme; this.line = line; this.column = column; this.length = length;
    }
    @Override public String toString() {
        return String.format("[%s] '%s' (L%d,C%d)", type, lexeme.replace("\n","\\n"), line, column);
    }
}
