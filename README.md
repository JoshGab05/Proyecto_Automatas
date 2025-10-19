# PythonLex (skeleton)
Proyecto modular con Maven + JFlex listo para empezar.

## Módulos
- pythonlex-core: modelos/contratos (Token, TokenType, AnalysisResult, Analyzer)
- pythonlex-lexer: integra JFlex y genera PythonLexer.java a partir de PythonLexer.flex
- pythonlex-engine: usa el lexer para analizar (incluye MainConsole de prueba)
- pythonlex-gui: placeholder; añadiremos Swing en el siguiente paso

## Build
mvn -pl pythonlex-core clean install

mvn -pl pythonlex-lexer clean install

mvn -pl pythonlex-gui clean compile

#Ejecutar el grafico

mvn -q -pl pythonlex-gui exec:java

