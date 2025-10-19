package com.pythonlex.core;

public class Token {
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;
    private final int length;

    // ✅ Constructor que el lexer está invocando (4 args)
    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, line, column, lexeme != null ? lexeme.length() : 0);
    }

    // Constructor completo (5 args)
    public Token(TokenType type, String lexeme, int line, int column, int length) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.length = length;
    }

    public TokenType getType() { return type; }
    public String getLexeme() { return lexeme; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public int getLength() { return length; }

    @Override public String toString() {
        return type + " '" + lexeme + "' (L" + line + ",C" + column + ")";
    }
}
