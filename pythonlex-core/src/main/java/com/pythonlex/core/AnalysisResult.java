package com.pythonlex.core;
import java.util.List;
public class AnalysisResult {
    public final List<Token> tokens;
    public final Token firstError;
    public AnalysisResult(List<Token> tokens, Token firstError) { this.tokens = tokens; this.firstError = firstError; }
}
