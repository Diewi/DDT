▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
Bar.foo foo = 2;
#STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case -templated
#PARSE(DECLARATION)   Bar.foo foo(A) = 2;
#STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol TemplateTypeParam(DefSymbol) Integer)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case -templated - c-style suffix
#PARSE(DECLARATION)   Bar.foo foo(A : B) #error(EXP_OPEN_PARENS)【】 #@BREAK [] = 2;
#STRUCTURE_EXPECTED:
DefinitionFunction(RefQualified(RefIdentifier RefIdentifier) DefSymbol TemplateTypeParam(DefSymbol RefIdentifier) )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_BROKEN__NoTypeAppend /*NoMissingExp*/ #@SEMICOLON_OR_BREAK #@IGNORE_REST fooB 
#STRUCTURE_EXPECTED:  IncompleteDeclarator(#@TYPEREF_BROKEN__NoTypeAppend)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE__NoTM #@NO_ID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  IncompleteDeclarator(#@TYPEREF_NBNE) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE__NoTM fooB #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE__NoTM DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE__LITE fooA (#@TPL_PARAMS__SAMPLE) = #@INITIALIZERS_TESTER #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE__LITE DefSymbol #@TPL_PARAMS__SAMPLE #@INITIALIZERS_TESTER)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with fragment
#PARSE(DECLARATION)   #@TYPEREF_NBNE__LITE fooA, fooB = #@INITIALIZERS_TESTER #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE__LITE DefSymbol DefVarFragment(DefSymbol #@INITIALIZERS_TESTER))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PREFIX《
  ►#?STRUCTURE_EXPECTED!【prefix,  ●DefSymbol 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  ●DefSymbol Integer 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  prf2 = 123,●DefSymbol Integer DefVarFragment(* Integer) 】●
  ►#?STRUCTURE_EXPECTED!【prefix, #@NO_ID, ●DefSymbol DefVarFragment(#@NO_DEFID) 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)              #@TYPEREF_NBNE__LITE #@PREFIX fooB = #@INITIALIZERS__SAMPLE #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED: DefVariable(#@TYPEREF_NBNE__LITE #@PREFIX DefVarFragment(DefSymbol #@INITIALIZERS__SAMPLE))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls (deprecated)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[] #@【 /**/ ● #error(EXP_SEMICOLON) #@BREAK * 】  ;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[10] = 2, foo2 #error(EXP_SEMICOLON) #@BREAK [10];
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefIndexing(CStyleRootRef Integer) Integer DefVarFragment(?) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[  /**/  #error(EXP_CLOSE_BRACKET) #@【 /**/ ● #error(EXP_SEMICOLON)#@BREAK = 2 】;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  auto foo[] #@NO_DEFID; // Make sure this is not confused with C-Style decl
#STRUCTURE_EXPECTED: DeclarationAttrib( AttribBasic IncompleteDeclarator(*) )
