/* 
 * Grammar for the Wool-W compiler, CS4533 and CS544, Worcester Polytechnic Institute
 * Author: Gary F. Pollice
 */
grammar Wool;

@header{
	package wool.lexparse;
}
 
// Parser rules
program                 :   classes += cls+ EOF;       
              								
cls : CLASS className=TYPE inhrt? OC (classVars+=vardef | classMeth+=method)* CC;
inhrt : INHERITS inhrtType=TYPE;
vardef: ID TS varType=TYPE varInit=assign? EL;
method : ID LP ((methForms+=formal PS)* methForms+=formal)* RP TS methType=TYPE OC methVars+=vardef* expr CC;
formal : ID TS formType=TYPE;
expr : 	
		LP expr RP									#ParenExpr
	| obj=expr DOT meth=ID LP params? RP			#ObjMeth
	| meth=ID LP params? RP							#LocMeth
	| IF cond=expr THEN then=expr ELSE els=expr FI 	#IfExpr
	| WHILE cond=expr LOOP body=expr POOL			#WhileExpr
	| OC (exprs+=expr EL)+ CC						#BlockExpr
	| SELECT selectPart+ END						#SelectExpr
	| NEW obj=TYPE									#NewObj
	| MINUS expr									#NegNumExpr
	| ISNULL expr 									#NullCheck
	| left=expr TIMES right=expr					#MulExpr
	| left=expr DIV right=expr						#DivExpr
	| left=expr PLUS right=expr						#AddExpr
	| left=expr MINUS right=expr					#SubExpr
	| left=expr LTE right=expr						#LTE_Expr
	| left=expr LT right=expr						#LT_Expr
	| left=expr EQ right=expr						#EQ_Expr
	| left=expr AE right=expr						#AE_Expr
	| left=expr GT right=expr						#GT_Expr
	| left=expr GTE right=expr						#GTE_Expr
	| NEG expr										#Neg_Log_Expr
	| ID assign 									#AssignExpr
	| ID 											#idExpr
	| NUM											#intExpr
	| STRING										#strExpr
	| TRUE											#trueExpr
	| FALSE 										#falseExpr
	| NULL											#nullExpr
	;
assign : ASSIGN expr;
selectPart : (expr TS expr EL);
	
params : (expr (PS expr)*);


// Lexer Rules
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