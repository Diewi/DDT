/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.ModelElementTestUtils;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.TemplateParameter;

public class DeeModelElement_Test extends BaseDeeTest implements ITestResourcesConstants {
	
	public static ISourceModule getSourceModule(String srcFolder, String cuPath) {
		ISourceModule sourceModule = SampleMainProject.getSourceModule(srcFolder, cuPath);
		assertTrue(sourceModule.exists());
		return sourceModule;
	}
	
	/*  ------  */
	public static boolean defunitIsReportedAsModelElement(DefUnit defunit) {
		boolean result = 
				defunit instanceof FunctionParameter || 
				defunit instanceof TemplateParameter ||
				defunit instanceof ImportSelectiveAlias ||
				defunit instanceof ImportAlias;
		return !result;
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		ISourceModule srcModule = getSourceModule(TR_SAMPLE_SRC1, "sampledefs.d");
		final IType topLevelElement = srcModule.getType("sampledefs");
		
		new SampleModelElementsVisitor(srcModule) { 
			@Override
			public void visitAllModelElements(
				IType _Module,
				IField _Variable,
				IField _Variable2,
				IField _VarExtended,
				IField _VarExtended2,
				IField _AutoVar,
				IField _AutoVar2,
				
				IMethod _Function,
				IMethod _AutoFunction,
				
				IType _Struct,
				IType _Union,
				IType _Class,
				IType _Interface,
				IType _Template,
				IType _Enum,
				IField _Enum_memberA,
				IField _Enum_memberB,
				
				IType _Mixin,
				IType _AliasVarDecl,
				IType _AliasFunctionDecl,
				IType _AliasFrag,
				IType _AliasFrag2,
				IField _OtherClass_fieldA,
				IMethod _OtherClass_methodB,
				IMethod _OtherClass_this,
				IType _OtherTemplate_TplNestedClass,
				IMethod tplFunc
			) {
				
				runCheckElementExists( _Module, EArcheType.Module, "module sampledefs;");
		
				runCheckElementExists(_Variable, EArcheType.Variable, "int Variable");
				runCheckElementExists(_Variable2, EArcheType.Variable, "Variable2");
				runCheckElementExists(_VarExtended, EArcheType.Variable, "/** DDOC */\r\nstatic ");
				runCheckElementExists(_VarExtended2, EArcheType.Variable, "VarExtended2");
				
				
				runCheckElementExists(_AutoVar, EArcheType.Variable, "auto AutoVar =");
				runCheckElementExists(_AutoVar2, EArcheType.Variable, "AutoVar2");
				
				runCheckElementExists(_Function, EArcheType.Function, "void Function(int fooParam)");
				runCheckElementExists(_AutoFunction, EArcheType.Function, "static AutoFunction(int fooParam)");
				
				runCheckElementExists(_Struct, EArcheType.Struct, "struct Struct { }");
				runCheckElementExists(_Union, EArcheType.Union, "union Union { }");
				runCheckElementExists(_Class, EArcheType.Class, "class Class {");
				runCheckElementExists(_Interface, EArcheType.Interface, "interface Interface { }");
		
				runCheckElementExists(_Template, EArcheType.Template, "template Template(");
				runCheckElementExists(_Enum, EArcheType.Enum, "enum Enum {");
				runCheckElementExists(_Enum_memberA, EArcheType.EnumMember, "EnumMemberA");
				runCheckElementExists(_Enum_memberB, EArcheType.EnumMember, "EnumMemberB");
				
				
				runCheckElementExists(_Mixin, EArcheType.Mixin, "mixin foo!() Mixin;");
				
				runCheckElementExists(_AliasVarDecl, EArcheType.Alias, "alias TargetFoo AliasVarDecl;");
				runCheckElementExists(_AliasFunctionDecl, EArcheType.Alias, "alias TargetFoo AliasFunctionDecl(");
				runCheckElementExists(_AliasFrag, EArcheType.Alias, "alias AliasFrag = int");
				runCheckElementExists(_AliasFrag2, EArcheType.Alias, "AliasFrag2 = char");
				
				runCheckElementExists(_OtherClass_fieldA, EArcheType.Variable, "int fieldA;");
				runCheckElementExists(_OtherClass_methodB, EArcheType.Function, "void methodB() { }");
				runCheckElementExists(_OtherClass_this, EArcheType.Constructor, "this(int ctorParam)");
				
				runCheckElementExists(_OtherTemplate_TplNestedClass, EArcheType.Class, "class TplNestedClass  {");
				runCheckElementExists(tplFunc, EArcheType.Function, "void tplFunc(asdf.qwer parameter) {");
			}
			
			protected void runCheckElementExists(IMember element, EArcheType archeType, String code) {
				checkElementExists(srcModule, element, archeType, code);
			}
			
		}.visitAll();
		
		assertTrue(topLevelElement.getMethod("OtherFunction").exists());
		// TODO: need to re-enable this test, but need to complete search engine tests first
		if(false) {
			assertTrue(topLevelElement.getMethod("OtherFunction").getChildren().length == 0);
		}
	}
	
	protected void checkElementExists(ISourceModule sourceModule, IMember element, EArcheType archeType, 
			String code) {
		try {
			checkElementExists(sourceModule, element, archeType, (String) null, code);
		} catch(ModelException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected void checkElementExists(ISourceModule sourceModule, IMember element, EArcheType archeType, 
		String nameKey, String code) throws ModelException {
		String source = sourceModule.getSource();
		
		assertTrue(element.exists());
		assertTrue(element.getCorrespondingResource() == null);
		assertTrue(element.getOpenable() == sourceModule);
		assertTrue(element.getSource().startsWith(code));
		int nameOffset = (nameKey == null) ? 
				source.indexOf(" " + element.getElementName()) + 1 :
				source.indexOf(nameKey) + nameKey.length() + 1;
		assertTrue(element.getNameRange().getOffset() == nameOffset);
		assertTrue(element.getNameRange().getLength() == element.getElementName().length());
		
		assertTrue(DeeModelElementUtil.elementFlagsToArcheType(element.getFlags()) == archeType);
		if(element instanceof IMethod) {
			IMethod method = (IMethod) element;
			assertTrue(method.isConstructor() == DeeModelElementUtil.isConstructor(element.getFlags()));
		}
		
		if(element.getNamespace() == null) {
			assertTrue(element.getParent() != sourceModule.getParent());
		} else {
			assertEquals(element.getNamespace().getQualifiedName("."), sourceModule.getParent().getElementName());
		}
	}
	
	@Test
	public void testImplicitModuleName() throws Exception { testImplicitModuleName$(); }
	public void testImplicitModuleName$() throws Exception {
		ISourceModule srcModule = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclImplicitName.d");
		assertEquals(srcModule.getElementName(), "moduleDeclImplicitName.d");
		
		assertTrue(ModelElementTestUtils.getChildren(srcModule, "moduleDeclImplicitName").size() > 0);
		
		IType topLevelElement = srcModule.getType("moduleDeclImplicitName");
		
		assertTrue(topLevelElement.getNameRange().getOffset() == -1);
		
		checkElementExists(srcModule, topLevelElement.getType("Foo"), 
			EArcheType.Class, "class Foo");
		checkElementExists(srcModule, topLevelElement.getType("Foo").getMethod("func"), 
			EArcheType.Function, "void func()");
		
	}
	
	@Test
	public void testMismatchedModuleName() throws Exception { testMismatchedModuleName$(); }
	public void testMismatchedModuleName$() throws Exception {
		ISourceModule incorrectNameMod = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclIncorrectName.d");
		assertEquals(incorrectNameMod.getElementName(), "moduleDeclIncorrectName.d");
		
		checkElementExists(incorrectNameMod, incorrectNameMod.getType("actualModuleName_DifferentFromFileName"), 
				EArcheType.Module, "module actualModuleName_DifferentFromFileName;");
	}
	
	@Test
	public void testNameSpace() throws Exception { testNameSpace$(); }
	public void testNameSpace$() throws Exception {
		IType topLevelElement;
		topLevelElement = getTopLevelElement(TR_SAMPLE_SRC1, "/", "sampledefs");
		testNameSpace(topLevelElement, "", "Class");
		
		topLevelElement = getTopLevelElement(TR_SAMPLE_SRC3, "pack/", "mod1");
		testNameSpace(topLevelElement, "pack", "Mod1Class");
		
		topLevelElement = getTopLevelElement(TR_SAMPLE_SRC3, "pack/subpack/","mod3");
		testNameSpace(topLevelElement, "pack.subpack", "Mod3Class");
		
		topLevelElement = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclImplicitName.d").
				getType("moduleDeclImplicitName");
		testNameSpace(topLevelElement, "", "Foo");
		
		topLevelElement = getSourceModule(TR_SAMPLE_SRC1, "src1pack/moduleDeclImplicitName2.d").
				getType("moduleDeclImplicitName2");
		testNameSpace(topLevelElement, "", "Foo");
	}
	
	protected IType getTopLevelElement(String srcFolder, String folderName, String moduleName) {
		return getSourceModule(srcFolder, folderName+"/"+moduleName+".d").getType(moduleName);
	}
	
	protected void testNameSpace(IType topLevelElement, String nameSpace, String sampleSubType) throws ModelException {
		assertEquals(topLevelElement.getNamespace().getQualifiedName("."), nameSpace);
		IType subElement = topLevelElement.getType(sampleSubType);
		assertTrue(subElement.exists() && subElement.getNamespace() == null);
	}
	
}