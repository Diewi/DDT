/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine;

import melnorme.lang.ide.core.engine.ILanguageServerHandler;

public class DeeLanguageServerHandler implements ILanguageServerHandler  {
	
	protected final DeeLanguageEngine languageEngine = new DeeLanguageEngine();
	
	@Override
	public void dispose() {
	}
	
	public DeeLanguageEngine getLanguageEngine() {
		return languageEngine;
	}
	
}