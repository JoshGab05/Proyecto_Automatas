package com.pythonlex.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba simple para verificar que las clases básicas del core funcionan.
 */
public class CoreSmokeTest {

    @Test
    void coreTypesExist() {
        // Crear un resultado de análisis
        AnalysisResult result = new AnalysisResult();

        // Agregar un token correcto (palabra reservada)
        result.addToken(new Token(TokenType.KEYWORD, "if", 1, 1));

        // Hasta ahora no hay errores
        assertTrue(result.isValid());

        // Agregar un token de error (carácter inválido)
        result.addToken(new Token(TokenType.ERROR, "@", 1, 3));

        // Debe marcarse como inválido
        assertFalse(result.isValid());
        assertEquals(1, result.getErrorLine());
        assertEquals(3, result.getErrorColumn());
        assertEquals("@", result.getErrorLexeme());
    }
}
