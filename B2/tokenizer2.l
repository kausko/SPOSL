%{
  FILE* yyin;
%}
digit	[0-9]
integer	{digit}*
floatingPoint	{digit}*\.{digit}*(E|e)?{digit}+
string	\"(\\.|[^"\\])*\"
identifier	[_a-zA-Z]+[_a-zA-Z0-9]*
unaryOperator	--|\+\+
binaryOperator	\+|-|\*|\/|%|<<|>>|>>>
comparisionOperator	<|>|<=|>=|==|!=|instance\sof
bitwiseOperator	&|\||\^
logicalOperator	&&|\|\|
assignmentOperator	\+=|-=|\*=|\/=|%=|<<=|>>=|>>>=|&=|\|=|\^=|=
loopKeywords	for|while|do
conditionalKeywords	if|else\sif|else|switch|case
exceptionKeywords	try|catch|finally
datatypes	byte|short|int|long|float|double|char|boolean|void
nullKeyword	null
booleanKeywords	true|false
accessSpecifier	public|private|protected
classTokens	class|static|final|abstract|implements|extends|interface|new
packageToken "package"[^\n]*";"
importToken	"import"[^\n]*";"
comment	(\/\/.*|\/\*(.|\n)*\*\/)
%%
{comment}	{printf("(Comment: %s)", yytext);}
{nullKeyword}	{printf("(Null: %s)", yytext);}
{packageToken}	{printf("(Package statement: %s)", yytext);}
{importToken}	{printf("(Import statement: %s)", yytext);}
{unaryOperator}	{printf("(Unary Operator: %s)", yytext);}
{binaryOperator}	{printf("(Binary Operator: %s)", yytext);}
{comparisionOperator}	{printf("(Comparision Operator: %s)", yytext);}
{bitwiseOperator}	{printf("(Bitwise Operator: %s)", yytext);}
{logicalOperator}	{printf("(Logical Operator: %s)", yytext);}
{assignmentOperator}	{printf("(Assignment Operator: %s)", yytext);}
{booleanKeywords}	{printf("(Boolean: %s)", yytext);}
{datatypes}	{printf("(Datatype: %s)", yytext);}
{loopKeywords}	{printf("(Loop Keyword: %s)", yytext);}
{conditionalKeywords}	{printf("(Conditional: %s)", yytext);}
{exceptionKeywords}	{printf("(Exception keyword: %s)", yytext);}
{accessSpecifier}	{printf("(Access Specifier: %s)", yytext);}
{classTokens}	{printf("(Class Token: %s)", yytext);}
{identifier}	{printf("(Identifier: %s)", yytext);}
{integer}	{printf("(Integer: %s)", yytext);}
{floatingPoint}	{printf("(Float: %s)", yytext);}
{string}	{printf("(String: %s)", yytext);}
%%

int yywrap() {
	return 1;
}

int main(int argc, char ** argv) {
	yyin = fopen(argv[1], "r");
	yylex();
	fclose(yyin);
}