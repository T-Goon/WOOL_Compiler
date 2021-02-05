/* 
 * Grammar for the Wool-W compiler, CS4533 and CS544, Worcester Polytechnic Institute
 * Author: Gary F. Pollice
 */
grammar Wool;

@header {
package wool.lexparse;
}

// Parser rules
program                 :   .*? EOF;       // Non-greedy just to not have a warning
              									// See (ANTLR) Sec. 15.6
//cls : CLASS WS [' inherits ' TYPE]? '{' [vardef|method]* '}';
//need to put "this" somewhere
// Should the lexer ignore comments?

BOOL : 'boolean';
CLASS : 'class';
ELSE : 'else';
END : 'end';
FALSE : 'false';
FI : 'fi';
IF : 'if';
IN : 'in';
INT : 'int';
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

ID : [a-z][a-zA-Z0-9_]*;
TYPE : [A-Z][a-zA-Z0-9_]*;
NUM : [0-9]+;

THIS : 'this';

WS : [\r\n] -> skip;
COMMENT1 : '#'.*[\n|EOF] -> skip;
COMMENT2 : '(*' .* '*)' SPACE* -> skip;
STRING : '"' ([a-zA-Z0-9 .,`~()$_={};:*&^%!#/'] | ESCP| SQRBRKTS)* '"';
fragment ESCP : '\\\\'|'\\r'| '\\t'| '\\b'| '\\f'| '\\\''| '\\"'| '\\\\n';
fragment SQRBRKTS : '[' | ']';
	
SPACE : [ |\t]+;