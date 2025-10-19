package com.pythonlex.engine;

import com.pythonlex.core.*;
import com.pythonlex.lexer.PythonLexerWrapper;

import java.io.File;

public class Analyzer {

    /** Analiza el archivo y devuelve el resultado. Detiene al primer error (lo hace el wrapper). */
    public static AnalysisResult analyzeFile(File file) throws Exception {
        PythonLexerWrapper wrapper = new PythonLexerWrapper();
        return wrapper.analyze(file);
    }

    /** Devuelve el texto coloreado concatenando los tokens. */
    public static String colorizedOutput(AnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        for (Token t : result.getTokens()) {
            sb.append(Colorizer.paint(t));
        }
        return sb.toString();
    }

    /** Mensaje de validación según rúbrica (línea y columna en caso de error). */
    public static String validationMessage(AnalysisResult result) {
        if (result.isValid()) {
            return "\n✅ El contenido del archivo es VÁLIDO.";
        }
        return "\n❌ Error léxico en línea " + result.getErrorLine()
             + ", columna " + result.getErrorColumn()
             + ". Lexema: '" + result.getErrorLexeme() + "'";
    }
}
