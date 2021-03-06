/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.IImportableUnit;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.SymbolTable;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefModule;

public class ImportContent extends ASTNode implements IImportFragment {
	
	public final RefModule moduleRef;
	
	public ImportContent(RefModule refModule) {
		this.moduleRef = parentize(refModule);
	}
	
	@Override
	protected CommonLanguageElement getParent_Concrete() {
		assertTrue(parent instanceof DeclarationImport || parent instanceof ImportSelective);
		return parent;
	}
	
	public DeclarationImport getDeclarationImport() {
		ILanguageElement parent = super.getLexicalParent();
		if(parent instanceof DeclarationImport) {
			return (DeclarationImport) parent;
		} else {
			return ((ImportSelective) parent).getDeclarationImport();
		}
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_CONTENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, moduleRef);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ImportContent(clone(moduleRef));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(moduleRef);
	}
	
	@Override
	public void setSemanticReady_afterChildren_do() {
		assertTrue(getDeclarationImport() != null);
	}
	
	@Override
	public RefModule getModuleRef() {
		return moduleRef;
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public void evaluateImportsScopeContribution(ScopeNameResolution scopeRes, boolean isSecondaryScope) {
		if(!isSecondaryScope) {
			// the static import part (package/module name) goes in the primary namespace.
			resolveStaticImport(scopeRes, moduleRef);
		} else {
			if(!getDeclarationImport().isStatic) {
				resolveContentImport(this, scopeRes);
			}
		}
	}
	
	public static void resolveStaticImport(ScopeNameResolution scopeRes, RefModule refModule) {
		INamedElement namedElement = refModule.createNamespaceFragment(scopeRes.getContext());
		scopeRes.visitNamedElement(namedElement);
	}
	
	public static void resolveContentImport(ImportContent impContent, ScopeNameResolution scopeRes) {
		IConcreteNamedElement targetModule = resolveTargetModule(scopeRes.getContext(), impContent);
		if(targetModule instanceof IImportableUnit) {
			IImportableUnit module = (IImportableUnit) targetModule;
			IScopeElement scopeElement = module.getImportableScope();
			
			SymbolTable moduleScopeNames = scopeRes.getLookup().resolveScopeSymbols(scopeElement);
			if(moduleScopeNames != null) {
				scopeRes.getNames().addSymbols(moduleScopeNames);
			}
		}
	}
	
	public static IConcreteNamedElement resolveTargetModule(ISemanticContext context, IImportFragment impSelective) {
		String[] packages = impSelective.getModuleRef().packages.getInternalArray();
		String moduleName = impSelective.getModuleRef().module;
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, moduleName));
		return CommonScopeLookup.resolveModule(context, impSelective, moduleFullName);
	}
	
}