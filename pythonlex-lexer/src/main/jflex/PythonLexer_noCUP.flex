package com.pythonlex.lexer;

import com.pythonlex.core.*;
import java.io.IOException;

%%

%public
%class PythonLexer
%unicode
%line
%column
%type Token
%function nextToken

/* =========================
   Macros y utilitarios
   ========================= */

/* Saltos de línea / espacio */
NEWLINE     = \r\n|\r|\n
WHITESPACE  = [ \t\f]+

/* Comentarios */
COMMENT     = \#.*

/* Identificadores (ASCII básico) */
ID          = [A-Za-z_][A-Za-z_0-9]*

/* Palabras reservadas (Python 3) */
RESERVED    = "False"|"None"|"True"|"and"|"as"|"assert"|"async"|"await"|"break"|"class"|"continue"|"def"|"del"|"elif"|"else"|"except"|"finally"|"for"|"from"|"global"|"if"|"import"|"in"|"is"|"lambda"|"nonlocal"|"not"|"or"|"pass"|"raise"|"return"|"try"|"while"|"with"|"yield"

/* -------- Números (con guiones bajos permitidos) -------- */

/* Enteros decimales (0 o no-cero con _ internos) */
NUM_DEC_INT = (0|[1-9](_?[0-9])*)
/* Binario, Octal, Hex */
NUM_BIN     = 0[bB](_?[01])+
NUM_OCT     = 0[oO](_?[0-7])+
NUM_HEX     = 0[xX](_?[0-9a-fA-F])+

/* Flotantes:
   1) ddd.ddd [exp]
   2) .ddd [exp]
   3) ddd. [exp]
   4) ddd [exp] (sin punto pero con exponente)
*/
NUM_INT_U   = [0-9](_?[0-9])*
NUM_FRAC_U  = [0-9](_?[0-9])*
EXP         = [eE][+-]?{NUM_INT_U}

FLOAT1      = {NUM_INT_U}\.{NUM_FRAC_U}({EXP})?
FLOAT2      = \.{NUM_FRAC_U}({EXP})?
FLOAT3      = {NUM_INT_U}\.({EXP})?
FLOAT4      = {NUM_INT_U}{EXP}
FLOAT       = {FLOAT1}|{FLOAT2}|{FLOAT3}|{FLOAT4}

/* Enteros en cualquier base */
INT         = {NUM_DEC_INT}|{NUM_BIN}|{NUM_OCT}|{NUM_HEX}

/* -------- Cadenas --------
   Prefijos: r|R|b|B|f|F y combinaciones típicas (fr, rf, rb, br, fb, bf).
   Nota: el análisis léxico no evalúa escapes; sólo consume correctamente.
*/
PFX1        = [rRbBfF]
PFX2        = ([rR][fF])|([fF][rR])|([rR][bB])|([bB][rR])|([fF][bB])|([bB][fF])
STRPFX      = ({PFX2}|{PFX1})?

/* Comillas dobles/simples: una línea (permite escapes) */
STRING_DQ   = {STRPFX}\"([^\"\\\n]|\\.)*\"
STRING_SQ   = {STRPFX}\'([^\'\\\n]|\\.)*\'

/* Triple comilla (multilínea). Greedy hasta siguiente delimitador triple. */
TRI_DQ      = {STRPFX}\"\"\"([^\\]|\\.|{NEWLINE})*\"\"\"
TRI_SQ      = {STRPFX}\'\'\'([^\\]|\\.|{NEWLINE})*\'\'\'

/* ====== EOF => null ====== */
%eofval{
  return null;
%eofval}

%%

/* ====== REGLAS ====== */

/* Comentarios */
{COMMENT}         { return new Token(TokenType.COMMENT,  yytext(), yyline+1, yycolumn+1); }

/* Cadenas (triple primero para no partirlas) */
{TRI_DQ}          { return new Token(TokenType.STRING,   yytext(), yyline+1, yycolumn+1); }
{TRI_SQ}          { return new Token(TokenType.STRING,   yytext(), yyline+1, yycolumn+1); }
{STRING_DQ}       { return new Token(TokenType.STRING,   yytext(), yyline+1, yycolumn+1); }
{STRING_SQ}       { return new Token(TokenType.STRING,   yytext(), yyline+1, yycolumn+1); }

/* Números (float antes que int) */
{FLOAT}           { return new Token(TokenType.NUMBER,   yytext(), yyline+1, yycolumn+1); }
{INT}             { return new Token(TokenType.NUMBER,   yytext(), yyline+1, yycolumn+1); }

/* Palabras reservadas */
{RESERVED}        { return new Token(TokenType.KEYWORD,  yytext(), yyline+1, yycolumn+1); }

/* Identificadores */
{ID}              { return new Token(TokenType.IDENT,    yytext(), yyline+1, yycolumn+1); }

/* Agrupadores (neutro/“GROUP”) */
"(" | ")" | "[" | "]" | "{" | "}" 
                  { return new Token(TokenType.GROUP,    yytext(), yyline+1, yycolumn+1); }

/* Operadores y puntuación (neutro). 
   Multi-caracter antes para evitar cortar (greedy correcto).
*/
":=" | "->" | "**=" | "//=" | "<<=" | ">>=" | "@=" | "+=" | "-=" | "*=" | "/=" | "%=" | "&=" | "|=" | "^=" | "=="
| "!=" | "<=" | ">=" | "**" | "//" | "<<" | ">>"
                  { return new Token(TokenType.OP,       yytext(), yyline+1, yycolumn+1); }

/* Simples */
":" | "," | "." | "..." | ";" | "+" | "-" | "*" | "/" | "%" | "@" | "=" | "<" | ">" | "&" | "|" | "^" | "~"
                  { return new Token(TokenType.OP,       yytext(), yyline+1, yycolumn+1); }

/* Saltos y espacio (se preservan para formato) */
{NEWLINE}         { return new Token(TokenType.NEWLINE,  "\\n",    yyline+1, yycolumn+1); }
{WHITESPACE}      { return new Token(TokenType.WHITESPACE,yytext(), yyline+1, yycolumn+1); }

/* Cualquier otro símbolo es error (detiene GUI) */
.                 { return new Token(TokenType.ERROR,    yytext(), yyline+1, yycolumn+1); }
