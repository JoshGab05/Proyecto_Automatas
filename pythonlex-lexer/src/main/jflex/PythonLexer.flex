/* Genera: target/generated-sources/jflex/com/pythonlex/lexer/PythonLexer.java */
package com.pythonlex.lexer;

import com.pythonlex.core.*;

%%
%public
%class PythonLexer
%unicode
%line
%column
%function nextToken
%type Token
%yylexthrow java.io.IOException

%eofval{
  return new Token(TokenType.EOF, "<EOF>", yyline+1, yycolumn+1, 0);
%eofval}

/* ===== Macros sencillas ===== */
WHITE = [ \t\r\f]+
NL    = (\n)
DQSTR = \"([^\\\n\"]|\\.)*\"
SQSTR = '([^\\\n']|\\.)*'
INT   = [0-9]+
ID    = [A-Za-z_][A-Za-z0-9_]*

%%

/* ===== Reglas mínimas y robustas ===== */
{WHITE}      { return new Token(TokenType.OTHER,   yytext(), yyline+1, yycolumn+1, yytext().length()); }
{NL}         { return new Token(TokenType.NEWLINE, yytext(), yyline+1, yycolumn+1, yytext().length()); }
#[^\n]*      { return new Token(TokenType.COMMENT, yytext(), yyline+1, yycolumn+1, yytext().length()); }
{DQSTR}      { return new Token(TokenType.STRING,  yytext(), yyline+1, yycolumn+1, yytext().length()); }
{SQSTR}      { return new Token(TokenType.STRING,  yytext(), yyline+1, yycolumn+1, yytext().length()); }
{INT}        { return new Token(TokenType.NUMBER,  yytext(), yyline+1, yycolumn+1, yytext().length()); }

/* Palabras reservadas: poner ANTES de ID para que tengan prioridad */
(False|None|True|and|as|assert|async|await|break|class|continue|def|del|elif|else|except|finally|for|from|global|if|import|in|is|lambda|nonlocal|not|or|pass|raise|return|try|while|with|yield)
            { return new Token(TokenType.KEYWORD, yytext(), yyline+1, yycolumn+1, yytext().length()); }

{ID}        { return new Token(TokenType.IDENT,    yytext(), yyline+1, yycolumn+1, yytext().length()); }

/* Fallback: TODO lo demás (operadores, puntuación, etc.) como OTHER (negro) */
[^]         { return new Token(TokenType.OTHER,    yytext(), yyline+1, yycolumn+1, yytext().length()); }
