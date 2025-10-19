package com.pythonlex.engine;

import com.pythonlex.core.Token;
import com.pythonlex.core.TokenType;

public final class Colorizer {
    private Colorizer() {}

    /** Devuelve el lexema coloreado según el tipo de token. */
    public static String paint(Token t) {
        String lex = t.getLexeme();
        if (lex == null) lex = "";

        return switch (t.getType()) {
            case KEYWORD    -> TokenColors.KEYWORD + lex + TokenColors.RESET;
            case NUMBER     -> TokenColors.NUMBER  + lex + TokenColors.RESET;
            case STRING     -> TokenColors.STRING  + lex + TokenColors.RESET;
            case COMMENT    -> TokenColors.COMMENT + lex + TokenColors.RESET;
            case IDENT      -> TokenColors.IDENT   + lex + TokenColors.RESET;
            case GROUP      -> TokenColors.GROUP   + lex + TokenColors.RESET;
            case OP         -> TokenColors.OP      + lex + TokenColors.RESET;
            case NEWLINE    -> "\n"; // respetar nueva línea
            case WHITESPACE -> lex;  // respetar espacios/tabs
            case ERROR      -> TokenColors.ERROR_BG + TokenColors.ERROR_FG + lex + TokenColors.RESET;
        };
    }
}
