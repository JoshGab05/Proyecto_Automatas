package com.pythonlex.core;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

/** Mapa de colores exigidos por el enunciado. */
public class ColorTheme {
    private final Map<TokenType, Color> foreground = new EnumMap<>(TokenType.class);
    private final Map<TokenType, Color> background = new EnumMap<>(TokenType.class);

    public ColorTheme() {
        // Reglas mínimas pedidas
        foreground.put(TokenType.KEYWORD, new Color(0x3B,0x5B,0xDB));   // azul/púrpura
        foreground.put(TokenType.NUMBER,  new Color(0xF5,0x8B,0x00));   // anaranjado
        foreground.put(TokenType.STRING,  new Color(0x1E,0x7D,0x32));   // verde oscuro
        foreground.put(TokenType.COMMENT, new Color(0x6A,0x1B,0x9A));   // morado

        // Neutros (agrupación, ops, lógica, ident) -> usa color por defecto (null)
        // ERROR -> fondo rojo, texto blanco
        foreground.put(TokenType.ERROR, Color.WHITE);
        background.put(TokenType.ERROR, new Color(0xD3,0x2F,0x2F));
    }

    public Color fg(TokenType t) { return foreground.get(t); }
    public Color bg(TokenType t) { return background.get(t); }
}
