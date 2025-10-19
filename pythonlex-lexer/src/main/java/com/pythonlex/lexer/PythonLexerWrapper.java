package com.pythonlex.lexer;

import com.pythonlex.core.*;
import java.io.*;

public class PythonLexerWrapper {

    public AnalysisResult analyze(File file) throws IOException {
        AnalysisResult result = new AnalysisResult();

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            PythonLexer lexer = new PythonLexer(reader);

            Token token;
            while ((token = lexer.nextToken()) != null) {
                result.addToken(token);
                // ❌ ya no nos detenemos: queremos recolectar TODO
            }
        }
        return result;
    }
}
