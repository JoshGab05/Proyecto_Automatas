package com.pythonlex.engine;
import com.pythonlex.core.*;
import com.pythonlex.lexer.PythonLexer;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
public class PythonAnalyzer implements Analyzer {
    @Override
    public AnalysisResult analyze(Reader reader) throws IOException {
        PythonLexer lex = new PythonLexer(reader);
        List<Token> tokens = new ArrayList<>();
        Token firstErr = null;
        while (true) {
            Token t = lex.nextToken();
            tokens.add(t);
            if (t.type == TokenType.ERROR && firstErr == null) firstErr = t;
            if (t.type == TokenType.EOF) break;
        }
        return new AnalysisResult(tokens, firstErr);
    }
}
