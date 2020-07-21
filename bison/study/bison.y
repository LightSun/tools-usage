%{
#include <stdio.h>
#include <stdlib.h>
#include "calc.h"
%}
%union{
   struct ast* a;
   double d;
}
%token <d> NUMBER
%token EOL

%type <a> exp factor term

%%
calclist:
  | calclist exp EOL {printf("=%4.4g\n",eval($2));
                      treefree($2);
                      printf("> ");
  }

  | calclist EOL {printf("> ");}
 ;
exp:factor
  | exp '+' factor{ $$=newcast('+',$1,$3);}
  | exp '-' factor{ $$=newcast('-',$1,$3);}
  ;
factor:term 
   | factor '*' term {$$=newcast('*',$1,$3);}
   | factor '/' term {$$=newcast('/',$1,$3);}
  ;
term: NUMBER {$$=newnum($1);}
   | '|' term{$$=newcast('|',$2,NULL);}
   | '(' exp ')' {$$=$2;}
   | '-' term {$$=newcast('M',$2,NULL);}
  ;
%%





