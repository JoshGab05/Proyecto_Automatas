package com.pythonlex.engine;
import com.pythonlex.core.*;
public class MainConsole {
    public static void main(String[] args) throws Exception {
        String demo = "print('hola')\n# comentario\n";
        Analyzer a = new PythonAnalyzer();
        AnalysisResult r = a.analyzeString(demo);
        for (Token t : r.tokens) System.out.println(t);
        if (r.firstError != null) {
            System.out.printf("ERROR en L%d, C%d%n", r.firstError.line, r.firstError.column);
        } else {
            System.out.println("Análisis válido.");
        }
    }
}
