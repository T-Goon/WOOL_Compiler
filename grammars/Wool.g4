/* 
 * Grammar for the Wool-W compiler, CS4533 and CS544, Worcester Polytechnic Institute
 * Author: Gary F. Pollice
 */
grammar Wool;

@header{
	package wool.lexparse;
}
 
// Parser rules
program                 :   cls+ EOF;       // Non-greedy just to not have a warning
              									// See (ANTLR) Sec. 15.6
cls : CLASS TYPE inhrt? OC (vardef | method)* CC;
inhrt : INHERITS TYPE;
vardef: ID TS TYPE assign? EL;
method : ID LP ((formal PS)* formal)* RP TS TYPE OC vardef* expr CC;
formal : ID TS TYPE;
expr : 	LP expr RP
	| expr DOT ID LP params? RP
	| ID LP params? RP
	| IF expr THEN expr ELSE expr FI
	| WHILE expr LOOP expr POOL
	| OC (expr EL)+ CC
	| SELECT (expr TS expr EL)+ END
	| NEW TYPE
	| MINUS expr
	| ISNULL expr 
	| expr TIMES expr
	| expr DIV expr
	| expr PLUS expr
	| expr MINUS expr
	| expr LTE expr
	| expr LT expr
	| expr EQ expr
	| expr AE expr
	| expr GT expr
	| expr GTE expr
	| NEG expr
	| ID assign 
	| ID 
	| NUM
	| STRING
	| TRUE
	| FALSE 
	| NULL;
assign : ASSIGN expr;
	
params : (expr (PS expr)*);
//need to put "this" somewhere
// Should the lexer ignore comments?

WS : [\r\n \t] -> skip;
LINECOMMENT : '#'.*? '\r'? ('\n'|EOF)  -> skip;
BLOCKCOMMENT : '(*' SUBCOMMENT*? '*)' -> skip;
fragment SUBCOMMENT : BLOCKCOMMENT | .;

CLASS : 'class';
ELSE : 'else';
END : 'end';
FALSE : 'false';
FI : 'fi';
IF : 'if';
IN : 'in';
INHERITS : 'inherits';
ISNULL : 'isnull';
LOOP : 'loop';
NEW : 'new';
NULL : 'null';
POOL : 'pool';
SELECT : 'select';
THEN : 'then';
TRUE : 'true';
WHILE : 'while';
ASSIGN :'<-';
PLUS : '+';
MINUS : '-';
TIMES : '*';
DIV : '/';
LT : '<';
LTE : '<=';
EQ : '=';
AE : '~=';
GTE : '>=';
GT : '>';
LP : '(';
RP : ')';
DOT : '.';
OC : '{';
CC : '}';
TS : ':';
EL : ';';
PS : ',';
NEG : '~';

TYPE : ([A-Z][a-zA-Z0-9_]* | 'int' | 'boolean');
ID : ([a-z][a-zA-Z0-9_]* | 'this');
NUM : [0-9]+;

STRING : '"' (~[\\"\n\r] | ESCP)* '"';
fragment ESCP : '\\\\'|'\\r'| '\\t'| '\\b'| 
'\\f'| '\\\''| '\\"'| '\\\n' | '\\n' | '\\\r\n';