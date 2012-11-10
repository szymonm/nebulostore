
grammar DQLGrammar;

options {
  language = Java;
  output=AST;
  ASTLabelType=CommonTree;  
}

tokens { 
  NEGATION;
}

@header {
  package org.nebulostore.query.language.interpreter.antlr;
}

@lexer::header {
  package org.nebulostore.query.language.interpreter.antlr;
}


ID  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT : '0'..'9'+
    ;

DOUBLE
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;
    

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

function_decl_parameters :
  ID ( ','!  ID)* ;

lambda_function : 
  'LAMBDA'^ function_decl_parameters ':' '('! expression ')'!;

call_parameter:
  expression ;

function_call_parameters :
  call_parameter  (','! call_parameter)* ;



function_call :
  ID^ '(' function_call_parameters? ')';

// http://javadude.com/articles/antlr3xtut/

STRING_LITERAL
  : '"' ( options { greedy = false;}: . )+ '"'
  ;

privacy_decl 
  : 'PRIVATE_MY'
  | 'PUBLIC_MY'
  | 'PRIVATE_COND' 
  | 'PUBLIC_OTHER'
  | 'PUBLIC_COND'
  ;


type_list :
  type  (','! type)* ;

type 
  : 'INTEGER'
  | 'DOUBLE'
  | 'STRING'
  | 'TUPLE'^ '<'! type_list '>'!
  | 'LIST'^ '<'! type '>'! 
  | 'FILE'
  ;


term
  : ID
  | '('! expression ')'!
  | INT
  | DOUBLE 
  | STRING_LITERAL
  | function_call
  | lambda_function 
  | 'TRUE'
  | 'FALSE'
  ;
  
negation
  : ('not'^)* term
  ;
  
unary
  : ('+'! | unary_negation_rewrite^)* negation
  ;
  
unary_negation_rewrite
  : '-' -> NEGATION
  ;
  
mult
  : unary (('*'^ | '/'^ | '%'^) unary)*
  ;


add
  : mult (('+'^ | '-'^) mult)*
  ;

relation
  : add (('='^ | '!='^ | '<'^ | '<='^ | '>='^ | '>'^) add)*
  ;
  
expression
  : relation (('&&'^ | '||'^) relation)* ( 'IS'^ privacy_decl ( 'AS'^ type )? )?
  ;


let : 'LET'! ID^ '='! expression; 

gather_statement 
  :   'GATHER'^ let+;

forward_statement
  : 'FORWARD'^ 'TO'! expression;

reduce_statement
  : 'REDUCE'^ expression EOF;

query :
   forward_statement
   gather_statement   
   reduce_statement
   ;