package com.pythonlex.engine;

/** Códigos ANSI para colorear en consola. */
public final class TokenColors {
    public static final String RESET   = "\u001B[0m";

    // Reglas del docente:
    // Palabras reservadas -> azul o púrpura (usar AZUL)
    public static final String KEYWORD = "\u001B[34m"; // azul

    // Números/constantes -> anaranjado (usaremos AMARILLO para aproximar consola)
    public static final String NUMBER  = "\u001B[33m"; // "orange" ≈ amarillo ANSI

    // Strings -> verde oscuro (usaremos verde)
    public static final String STRING  = "\u001B[32m";

    // Comentarios -> morado (magenta)
    public static final String COMMENT = "\u001B[35m";

    // Identificadores / variables -> color general (neutro)
    public static final String IDENT   = "\u001B[0m";

    // Signos de agrupación -> neutro
    public static final String GROUP   = "\u001B[0m";

    // Comparación / lógicos -> neutro
    public static final String OP      = "\u001B[0m";

    // Newline / Whitespace -> neutro (no coloreamos, los preservamos)
    public static final String NEUTRAL = "\u001B[0m";

    // Error -> fondo rojo texto blanco
    public static final String ERROR_BG = "\u001B[41m"; // fondo rojo
    public static final String ERROR_FG = "\u001B[97m"; // blanco intenso

    private TokenColors() {}
}
