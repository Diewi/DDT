/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text.hover;

import org.eclipse.dltk.internal.ui.BrowserInformationControl;
import org.eclipse.dltk.internal.ui.text.HTMLTextPresenter;
import org.eclipse.dltk.internal.ui.text.ScriptWordFinder;
import org.eclipse.dltk.ui.text.hover.IScriptEditorTextHover;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Mod from DLTK 3.0 that allows a different fImplementation other than a ScriptTypeHover
 */
public class ScriptInformationProvider_Mod implements IInformationProvider,
		IInformationProviderExtension2 {

	class EditorWatcher implements IPartListener {

		@Override
		public void partOpened(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part == fEditor) {
				fEditor.getSite().getWorkbenchWindow().getPartService()
						.removePartListener(fPartListener);
				fPartListener = null;
			}
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
			update();
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			update();
		}
	}

	protected IEditorPart fEditor;
	protected IPartListener fPartListener;

	protected String fCurrentPerspective;
	protected IScriptEditorTextHover fImplementation;

	/**
	 * The presentation control creator.
	 */
	private IInformationControlCreator fPresenterControlCreator;

	public ScriptInformationProvider_Mod(IEditorPart editor) {

		fEditor = editor;

		if (fEditor != null) {

			fPartListener = new EditorWatcher();
			IWorkbenchWindow window = fEditor.getSite().getWorkbenchWindow();
			window.getPartService().addPartListener(fPartListener);

			update();
		}
	}

	protected void update() {

		IWorkbenchWindow window = fEditor.getSite().getWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {

			IPerspectiveDescriptor perspective = page.getPerspective();
			if (perspective != null) {
				String perspectiveId = perspective.getId();

				if (fCurrentPerspective == null
						|| fCurrentPerspective != perspectiveId) {
					fCurrentPerspective = perspectiveId;

					fImplementation = createImplementation();
					fImplementation.setEditor(fEditor);
				}
			}
		}
	}

	protected IScriptEditorTextHover createImplementation() {
		return new ScriptTypeHover();
	}

	@Override
	public IRegion getSubject(ITextViewer textViewer, int offset) {

		if (textViewer != null)
			return ScriptWordFinder.findWord(textViewer.getDocument(), offset);

		return null;
	}

	@Override
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		if (fImplementation != null) {
			String s = fImplementation.getHoverInfo(textViewer, subject);
			if (s != null && s.trim().length() > 0) {
				return s;
			}
		}

		return null;
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null) {
			fPresenterControlCreator = new AbstractReusableInformationControlCreator() {

				@Override
				public IInformationControl doCreateInformationControl(
						Shell parent) {
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent,
								JFaceResources.DIALOG_FONT, true);
					else
						return new DefaultInformationControl(parent,
								new HTMLTextPresenter(false));
				}
			};
		}
		return fPresenterControlCreator;
	}
}
