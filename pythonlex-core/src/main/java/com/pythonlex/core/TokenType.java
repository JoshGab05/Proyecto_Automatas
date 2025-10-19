package com.pythonlex.core;

public enum TokenType {
    KEYWORD,     // if, def, class, etc.
    NUMBER,      // 123, 3.14
    STRING,      // "hola", 'hola'
    IDENT,       // variables/identificadores
    GROUP,       // ( ) [ ] { }
    OP,          // == != >= <= > < =
    COMMENT,     // # ...
    NEWLINE,     // fin de línea
    WHITESPACE,  // espacios/tabs
    ERROR        // símbolo inválido
}
