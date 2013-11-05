/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.debug.core;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunchDelegate;
import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

import dtool.DToolBundle;

public class DeeDebugLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {
	
	protected final GdbLaunchDelegate gdbLaunchDelegate = new GdbLaunchDelegateExtension();
	
	@Override
	public String getLanguageId() {
		return DeeNature.NATURE_ID;
	}
	
	
	
	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		return super.buildForLaunch(configuration, mode, monitor);
	}
	
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		
		// Remove some DLTK attributes that affect how our launch runs
		ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		workingCopy.removeAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_DEBUG_CONSOLE, false);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_USE_INTERACTIVE_CONSOLE, false);

		
		String progName = workingCopy.getAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME, 
				(String) null);
		String projectName = workingCopy.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
				(String) null);
		if(projectName == null) {
			abort("Project not specified", null);
		}
		IPath projectPath = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getLocation();
		IPath fullBinaryPath = projectPath.append(progName); 
		
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, fullBinaryPath.toString());

		String progArgs = workingCopy.getAttribute(ScriptLaunchConfigurationConstants.ATTR_SCRIPT_ARGUMENTS, 
				(String) null);
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, progArgs);

		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_USE_TERMINAL, 
				ICDTLaunchConfigurationConstants.USE_TERMINAL_DEFAULT);

		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, 
				"gdb");
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, 
				ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, 
				ICDTLaunchConfigurationConstants.DEBUGGER_STOP_AT_MAIN_DEFAULT);

		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL, 
				ICDTLaunchConfigurationConstants.DEBUGGER_STOP_AT_MAIN_SYMBOL_DEFAULT);

		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, 
				"gdb");
		
		
		workingCopy.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, 
				"gdb");
//		workingCopy.setAttribute(IGDBLaunchConfigurationConstants.ATTR_GDB_INIT, 
//				".gdbinit");
		workingCopy.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_NON_STOP, 
				IGDBLaunchConfigurationConstants.DEBUGGER_NON_STOP_DEFAULT);
		workingCopy.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_REVERSE, 
				IGDBLaunchConfigurationConstants.DEBUGGER_REVERSE_DEFAULT);
		
		
		workingCopy.setAttribute(ISourcePathComputer.ATTR_SOURCE_PATH_COMPUTER_ID, 
				"org.eclipse.cdt.debug.core.sourcePathComputer");
		
		
		workingCopy.doSave();
		
		return gdbLaunchDelegate.getLaunch(configuration, mode);
		
//		final Launch launch = new Launch(configuration, mode, null);
//		return launch;
	}
	
	@Override
	protected void setDebugConsoleAttributes(Launch launch, ILaunchConfiguration configuration) throws CoreException {
		throw assertUnreachable();
	}
	
	@Override
	protected void setDebugOptions(Launch launch, ILaunchConfiguration configuration) throws CoreException {
		throw assertUnreachable();
	}
	
	
	protected String savedAttrCaptureOutput;
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
		if(!mode.equals(ILaunchManager.DEBUG_MODE)) {
			abort("Internal error: can only use this delegate for debug launches", null);
		}
		
		gdbLaunchDelegate.launch(configuration, mode, launch, monitor);
		
	}
	
	@Override
	public IInterpreterRunner getInterpreterRunner(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		throw assertUnreachable();
	}
	
	@Override
	protected void runRunner(ILaunchConfiguration configuration, IInterpreterRunner runner, InterpreterConfig config,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		throw assertUnreachable();
	}
	
	public static class GdbLaunchDelegateExtension extends GdbLaunchDelegate {
		
		@Override
		public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
			return super.getLaunch(configuration, mode);
		}
		
		@Override
		protected ISourceLocator getSourceLocator(ILaunchConfiguration configuration, DsfSession session)
				throws CoreException {
			return super.getSourceLocator(configuration, session);
		}
		
		@Override
		protected IPath checkBinaryDetails(ILaunchConfiguration config) throws CoreException {
			// Now verify we know the program to debug.
			IPath exePath = LaunchUtils.verifyProgramPath(config, null);
			// Finally, make sure the program is a proper binary.
			
			// BM: this code is disabled because without a project the binary verifier defaults to ELF on any platform
			if(DToolBundle.UNIMPLEMENTED_FUNCTIONALITY) {
				LaunchUtils.verifyBinary(config, exePath);
			}
			return exePath;
		}
		
	}
	
}