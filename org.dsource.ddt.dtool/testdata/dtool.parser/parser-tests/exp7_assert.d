▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
assert(true)
#AST_STRUCTURE_EXPECTED:
ExpAssert(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
assert(#@EXP_ASSIGN, "messsage")
#AST_STRUCTURE_EXPECTED:
ExpAssert(#@EXP_ASSIGN String)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
assert( #@EXP1《 #@EXP_ASSIGN,●#error(EXPRULE_exp),●》 #@EXP2《#@EXP_ASSIGN●#error(EXPRULE_exp)》  
#@《)●#error(EXP_CLOSE_PARENS)》
#AST_STRUCTURE_EXPECTED:
ExpAssert(#@《 #@EXP_ASSIGN ● MissingExpression ●》(EXP1) #@《#@EXP_ASSIGN●MissingExpression》(EXP2) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
assert #error(EXP_OPEN_PARENS)
#AST_SOURCE_EXPECTED:
assert
#AST_STRUCTURE_EXPECTED:
ExpAssert()