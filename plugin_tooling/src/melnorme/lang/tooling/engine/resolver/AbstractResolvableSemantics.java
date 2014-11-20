/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.resolver;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class AbstractResolvableSemantics implements IResolvableSemantics {
	
	@Override
	public ResolutionResult resolveTargetElement(ISemanticContext sr) {
		return new ResolutionResult(findTargetDefElement(sr)); /*FIXME: BUG here null*/
	}
	
	// TODO: deprecate this method in favor of resolveTargetElement
	@Override
	public final INamedElement findTargetDefElement(ISemanticContext moduleResolver) {
		Collection<INamedElement> namedElems = findTargetDefElements(moduleResolver, true);
		if(namedElems == null || namedElems.isEmpty())
			return null;
		return namedElems.iterator().next();
	}
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
		Collection<INamedElement> resolvedElements = this.findTargetDefElements(mr, false);
		
		return resolveTypeOfUnderlyingValue(mr, resolvedElements); 
	}
	
	public static Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr, 
		Collection<INamedElement> resolvedElements) {
		ArrayList<INamedElement> resolvedTypeForValueContext = new ArrayList<>();
		for (INamedElement defElement : resolvedElements) {
			INamedElement resolveTypeForValueContext = defElement.resolveTypeForValueContext(mr);
			if(resolvedTypeForValueContext != null) {
				resolvedTypeForValueContext.add(resolveTypeForValueContext);
			}
		}
		return resolvedTypeForValueContext;
	}
	
}