grammar Antlr;

@header{
	package br.ufpe.cin.if688.minijava.main;
}



goal: mainClass (classDeclaration)* EOF;

mainClass : 'class'identifier '{' 'public' 'static' 'void' 'main''(' 'String' '[' ']' identifier ')' '{' statement '}' '}';

classDeclaration: 'class' identifier ('extends' identifier )? '{' (varDeclaration)* (methodDeclaration)* '}';

varDeclaration: type identifier ';';

methodDeclaration: 'public' type identifier '(' (type identifier (',' type identifier)*)? ')' '{' (varDeclaration)* (statement)* 'return' expression ';''}';
type: 'int' '['']'
	| 'boolean'
	| 'int'
	| identifier;


statement: '{' (statement)* '}'
			| 'if' '(' expression ')' statement 'else' statement
			| 'while' '(' expression ')' statement
			| 'System.out.println' '('expression')'';'
			| identifier '=' expression ';'
			| identifier '[' expression ']' '=' expression ';';

expression: expression ('&&' | '<' | '+' | '-' | '*') expression
			| expression '[' expression ']' 
			| expression '.''length'
			| expression '.' identifier '(' (expression(',' expression)* )? ')'
			| integer
			| 'true'
			| 'false' 
			| identifier
			| 'this'
			| 'new' 'int' '['expression']'
			| 'new' identifier '('')'
			| '!' expression
			| '(' expression ')';

identifier: IDENTIFIER;

integer: INTEGER;
IDENTIFIER: ((LETTER) | '_')((ALPHANUMERIC) | '_')*;
LETTER: [a-zA-z];
ALPHANUMERIC: (LETTER)| (DIGIT);
DIGIT: [0-9];
INTEGER: '0'
		 |((DIGITO)(DIGIT)*);
DIGITO: [1-9];
WS: [ \n\t\r\f] -> skip;
BREAKLINE: ([/n] | [/r/n]) -> skip;
COMMENT: '/*' .*? '*/' -> skip;






