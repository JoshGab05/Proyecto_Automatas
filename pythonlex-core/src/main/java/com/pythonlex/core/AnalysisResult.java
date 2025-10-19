package com.pythonlex.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalysisResult {
    private boolean valid = true;
    private int errorLine = -1;
    private int errorColumn = -1;
    private String errorLexeme = null;
    private final List<Token> tokens = new ArrayList<>();

    public AnalysisResult() { }

    public void addToken(Token t) {
        if (t.getType() == TokenType.ERROR && valid) {
            valid = false;
            errorLine = t.getLine();
            errorColumn = t.getColumn();
            errorLexeme = t.getLexeme();
        }
        tokens.add(t);
    }

    public boolean isValid() { return valid; }
    public int getErrorLine() { return errorLine; }
    public int getErrorColumn() { return errorColumn; }
    public String getErrorLexeme() { return errorLexeme; }
    public List<Token> getTokens() { return Collections.unmodifiableList(tokens); }
}
