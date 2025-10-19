package com.pythonlex.core;
import java.io.IOException;
import java.io.Reader;
public interface Analyzer {
    AnalysisResult analyze(Reader reader) throws IOException;
    default AnalysisResult analyzeString(String text) throws IOException {
        return analyze(new java.io.StringReader(text));
    }
}
