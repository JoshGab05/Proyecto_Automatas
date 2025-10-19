# PythonLex (skeleton)
Proyecto modular con Maven + JFlex listo para empezar.

## Módulos
- pythonlex-core: modelos/contratos (Token, TokenType, AnalysisResult, Analyzer)
- pythonlex-lexer: integra JFlex y genera PythonLexer.java a partir de PythonLexer.flex
- pythonlex-engine: usa el lexer para analizar (incluye MainConsole de prueba)
- pythonlex-gui: placeholder; añadiremos Swing en el siguiente paso

## Build
mvn -q -f pom.xml clean install

## Probar por consola (Engine)
# Windows (usa ';' en lugar de ':')
java -cp pythonlex-engine/target/classes;pythonlex-lexer/target/classes;pythonlex-core/target/classes com.pythonlex.engine.MainConsole

# Linux/macOS
java -cp pythonlex-engine/target/classes:pythonlex-lexer/target/classes:pythonlex-core/target/classes com.pythonlex.engine.MainConsole
