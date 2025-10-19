package com.pythonlex.engine;

import com.pythonlex.core.AnalysisResult;

import java.io.File;

public class MainConsole {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: mvn -q -pl pythonlex-engine exec:java -Dexec.args=\"ruta\\al\\archivo.py\"");
            System.exit(1);
        }
        File f = new File(args[0]);
        if (!f.exists()) {
            System.out.println("No se encontró el archivo: " + f.getAbsolutePath());
            System.exit(1);
        }
        try {
            AnalysisResult result = Analyzer.analyzeFile(f);

            // 1) Imprime los tokens coloreados
            String colored = Analyzer.colorizedOutput(result);
            System.out.println(colored);

            // 2) Imprime el resultado de validación
            System.out.println(Analyzer.validationMessage(result));

        } catch (Exception ex) {
            System.err.println("Fallo al analizar: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
