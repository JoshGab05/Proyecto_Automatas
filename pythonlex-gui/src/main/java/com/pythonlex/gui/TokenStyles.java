package com.pythonlex.gui;

import com.pythonlex.core.TokenType;

import java.awt.*;

public final class TokenStyles {
    private TokenStyles(){}

    public static Color fg(TokenType type) {
        return switch (type) {
            case KEYWORD    -> new Color(0x7C, 0xA6, 0xFF); // azul claro
            case NUMBER     -> new Color(0xFF, 0xB0, 0x4A); // naranja claro
            case STRING     -> new Color(0x7C, 0xD9, 0x7C); // verde claro
            case COMMENT    -> new Color(0xD0, 0x8C, 0xF0); // morado claro
            case IDENT      -> new Color(0xE6, 0xE6, 0xE6); // gris muy claro
            case GROUP      -> new Color(0xE6, 0xE6, 0xE6); // neutro
            case OP         -> new Color(0xE6, 0xE6, 0xE6); // neutro
            case NEWLINE    -> new Color(0xE6, 0xE6, 0xE6); // no se usa (se inserta \n)
            case WHITESPACE -> new Color(0xE6, 0xE6, 0xE6); // no se usa (se inserta tal cual)
            case ERROR      -> Color.WHITE;                 // texto blanco sobre rojo
        };
    }

    public static Color bg(TokenType type) {
        return switch (type) {
            case ERROR      -> new Color(0xB0, 0x12, 0x12); // rojo oscuro
            default         -> null;
        };
    }

    /** NEWLINE y WHITESPACE se insertan tal cual. */
    public static boolean isLiteralInsert(TokenType type) {
        return type == TokenType.NEWLINE || type == TokenType.WHITESPACE;
    }
}
