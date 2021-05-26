%{
  int lines, words, chars;
%}

%%
\n { chars++; lines++; }
[^ \n\t]+ { words++; chars+=yyleng;}
. { chars++; }
%%

int yywrap(void) { 
  return 1; 
}

int main(int argc, char ** argv) {
  yyin = fopen(argv[1], "r");
  yylex();
  printf("%d lines,\n%d words,\n%d characters\n", chars > 0 ? lines + 1 : lines, words, chars);
  return 0;
}
