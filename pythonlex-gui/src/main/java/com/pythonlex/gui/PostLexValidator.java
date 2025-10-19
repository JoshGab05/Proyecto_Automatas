package com.pythonlex.gui;

import com.pythonlex.core.Token;
import com.pythonlex.core.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** Validador post-léxico: acumula TODOS los problemas encontrados. */
public final class PostLexValidator {

    /** Un problema detectado en la secuencia de tokens. */
    public static final class Issue {
        public final int index;     // índice del token al que se asocia
        public final String message;
        public final boolean replaceStyleOnly; // true: solo resaltar el token existente
        public final String expectedLexeme;    // si no hay token (faltante), lexema sugerido (p.ej. ":" o ")")
        public Issue(int index, String message, boolean replaceStyleOnly, String expectedLexeme) {
            this.index = index; this.message = message; this.replaceStyleOnly = replaceStyleOnly; this.expectedLexeme = expectedLexeme;
        }
    }

    private PostLexValidator(){}

    public static List<Issue> validateAll(List<Token> ts) {
        List<Issue> issues = new ArrayList<>();

        // 0) Errores LÉXICOS tal cual (los marcamos donde aparecen)
        for (int i = 0; i < ts.size(); i++) {
            if (ts.get(i).getType() == TokenType.ERROR) {
                Token t = ts.get(i);
                issues.add(new Issue(i, "Error léxico en L"+t.getLine()+", C"+t.getColumn()+" — \""+t.getLexeme()+"\"",
                        true, null));
            }
        }

        // 1) Balanceo global de (), [], {}
        Stack<Token> st = new Stack<>();
        for (int i = 0; i < ts.size(); i++) {
            Token t = ts.get(i);
            if (t.getType() == TokenType.GROUP) {
                String s = t.getLexeme();
                if ("(".equals(s) || "[".equals(s) || "{".equals(s)) st.push(t);
                else if (")".equals(s) || "]".equals(s) || "}".equals(s)) {
                    if (st.isEmpty() || !match(st.peek().getLexeme(), s)) {
                        issues.add(new Issue(i, "Cierre sin apertura: '"+s+"'", true, null));
                    } else st.pop();
                }
            }
        }
        if (!st.isEmpty()) {
            Token open = st.peek();
            String expected = switch (open.getLexeme()) { case "("->")"; case "["->"]"; case "{"->"}"; default -> "?"; };
            issues.add(new Issue(indexOfLastToken(ts), "Falta cerrar '"+open.getLexeme()+"' → se esperaba '"+expected+"'", false, expected));
        }

        // 2) def headers
        for (int i = 0; i < ts.size(); i++) {
            if (isKw(ts, i, "def")) checkDefHeader(ts, i, issues);
        }

        // 3) class headers
        for (int i = 0; i < ts.size(); i++) {
            if (isKw(ts, i, "class")) checkClassHeader(ts, i, issues);
        }

        // 4) Palabras que requieren ':' al final de la línea
        String[] needColon = {"if","elif","else","for","while","try","except","finally","with"};
        for (int i = 0; i < ts.size(); i++) {
            if (ts.get(i).getType() == TokenType.KEYWORD) {
                for (String k : needColon) if (k.equals(ts.get(i).getLexeme())) {
                    requireColonBeforeNewline(ts, i, issues);
                }
            }
        }
        return issues;
    }

    // ------ helpers ------

    private static boolean match(String o, String c) {
        return ("(".equals(o) && ")".equals(c)) || ("[".equals(o) && "]".equals(c)) || ("{".equals(o) && "}".equals(c));
    }
    private static boolean isKw(List<Token> ts, int i, String k){ return i>=0 && i<ts.size() && ts.get(i).getType()==TokenType.KEYWORD && k.equals(ts.get(i).getLexeme()); }
    private static boolean has(List<Token> ts, int i){ return i>=0 && i<ts.size(); }
    private static boolean isGroup(Token t, String s){ return t.getType()==TokenType.GROUP && s.equals(t.getLexeme()); }
    private static int indexOfLastToken(List<Token> ts){ return Math.max(0, ts.size()-1); }

    private static void checkDefHeader(List<Token> ts, int idxDef, List<Issue> issues) {
        int i = idxDef + 1;

        if (!has(ts, i) || ts.get(i).getType()!=TokenType.IDENT) {
            issues.add(new Issue(idxDef, "Tras 'def' debe venir el nombre de la función", false, "<ident>"));
            return;
        }
        i++;
        if (!has(ts, i) || !isGroup(ts.get(i), "(")) {
            issues.add(new Issue(i-1, "Falta '(' tras el nombre de la función", false, "("));
            return;
        }
        i++;
        int par = 1;
        while (has(ts, i) && par > 0) {
            Token t = ts.get(i);
            if (isGroup(t, "(")) par++;
            else if (isGroup(t, ")")) par--;
            else if (t.getType()==TokenType.OP && "->".equals(t.getLexeme())) {
                issues.add(new Issue(i, "El operador '->' debe ir después de ')'", true, null));
            } else if (t.getType()==TokenType.NEWLINE) {
                issues.add(new Issue(i-1, "Falta ')' en cabecera de función", false, ")"));
                return;
            }
            i++;
        }
        if (!has(ts, i-1) || !isGroup(ts.get(i-1), ")")) {
            issues.add(new Issue(Math.max(i-1, idxDef), "Falta ')' en cabecera de función", false, ")"));
            return;
        }
        // optional '->' retorno
        if (has(ts, i) && ts.get(i).getType()==TokenType.OP && "->".equals(ts.get(i).getLexeme())) {
            i++;
            while (has(ts, i) && ts.get(i).getType()!=TokenType.NEWLINE && !(ts.get(i).getType()==TokenType.OP && ":".equals(ts.get(i).getLexeme()))) {
                i++;
            }
        }
        // ':'
        if (!has(ts, i) || ts.get(i).getType()==TokenType.NEWLINE) {
            issues.add(new Issue(Math.max(i-1, idxDef), "Falta ':' al final de la cabecera de función", false, ":"));
        }
    }

    private static void checkClassHeader(List<Token> ts, int idxClass, List<Issue> issues) {
        int i = idxClass + 1;
        if (!has(ts, i) || ts.get(i).getType()!=TokenType.IDENT) {
            issues.add(new Issue(idxClass, "Tras 'class' debe venir el nombre de la clase", false, "<ident>"));
            return;
        }
        i++;
        if (has(ts, i) && isGroup(ts.get(i), "(")) {
            int par = 1; i++;
            while (has(ts, i) && par > 0) {
                if (isGroup(ts.get(i), "(")) par++;
                else if (isGroup(ts.get(i), ")")) par--;
                else if (ts.get(i).getType()==TokenType.NEWLINE) break;
                i++;
            }
            if (par > 0) {
                issues.add(new Issue(Math.max(i-1, idxClass), "Falta ')' en cabecera de clase", false, ")"));
                return;
            }
        }
        requireColonBeforeNewline(ts, i-1, issues);
    }

    private static void requireColonBeforeNewline(List<Token> ts, int anchorIdx, List<Issue> issues) {
        int i = anchorIdx + 1;
        while (has(ts, i) && ts.get(i).getType()!=TokenType.NEWLINE) {
            if (ts.get(i).getType()==TokenType.OP && ":".equals(ts.get(i).getLexeme())) return;
            i++;
        }
        issues.add(new Issue(Math.max(anchorIdx, 0), "Falta ':' al final de la sentencia", false, ":"));
    }
}
