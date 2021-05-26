%{
#include <stdio.h>
#include "y.tab.h"
%}

DIGIT [0-9]
REAL {DIGIT}+[.]{DIGIT}*
LETTER [A-Za-z]
ASSIGN =
%%
[\t ] ;
int {printf("Datatype: %s\n",yytext);return (INT);}
float {printf("Datatype: %s\n",yytext);return (FLOAT);}
char {printf("Datatype: %s\n",yytext);return (CHAR);}
boolean {printf("Datatype: %s\n",yytext);return (BL);}
true|false { printf("Boolean value: %s\n",yytext);return BLVAL;}
['][^\t\n]['] { printf("Character value: %s\n",yytext);return CHVAL;}
[a-zA-z]+[a-zA-z0-9_]*  {printf("ID: %s\n",yytext);return ID;}
{REAL} { printf("Real number: %s\n",yytext);return REAL;}
{DIGIT}+ { printf("Int number: %s\n",yytext);return NUM;}
"," {printf("Comma:  %s\n",yytext);return COMMA;}
";" {printf("SC: %s\n",yytext);;return SC;}
{ASSIGN} {printf("Assign: %s\n",yytext);return AS;}
\n return NL;
. ;

%%

int yywrap() {
return 1;
}