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
package melnorme.lang.ide.core;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import melnorme.lang.ide.core.engine.SourceModelManager;
import melnorme.lang.ide.core.operations.AbstractToolManager;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.project_model.BundleModelManager;
import melnorme.lang.ide.core.project_model.LangBundleModel;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.tooling.data.StatusException;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.ILogHandler;

public abstract class LangCore extends Plugin {
	
	public static final String PLUGIN_ID = LangCore_Actual.PLUGIN_ID;
	public static final String TESTS_PLUGIN_ID = PLUGIN_ID + ".tests";
	
	public static final String NATURE_ID = LangCore_Actual.NATURE_ID;
	
	protected static LangCore pluginInstance;
	
	/** Returns the singleton for this plugin instance. */
	public static LangCore getInstance() {
		return pluginInstance;
	}
	
	protected LangCore2 langCore;
	
	/* ----------------- Owned singletons: ----------------- */
	
	public static AbstractToolManager getToolManager() {
		return pluginInstance.langCore.toolManager;
	}
	public static SourceModelManager getSourceModelManager() {
		return pluginInstance.langCore.sourceModelManager;
	}
	public static BundleModelManager<?> getBundleModelManager() {
		return pluginInstance.langCore.bundleManager;
	}
	public static LangBundleModel getBundleModel() {
		return pluginInstance.langCore.bundleManager.getModel();
	}
	public static BuildManager getBuildManager() {
		return pluginInstance.langCore.buildManager;
	}
	
	/* -----------------  ----------------- */
	
	protected boolean initializedAfterUI = false;
	
	@Override
	public final void start(BundleContext context) throws Exception {
		pluginInstance = this;
		LangCore2 langCore2 = new LangCore2();
		assertTrue(this.langCore == langCore2);
		super.start(context);
		doCustomStart(context);
	}
	
	protected abstract void doCustomStart(BundleContext context);
	
	/** 
	 * Initialize services that should only be started after the UI plugin 
	 * (or other application plugin such as test runner) has started.
	 * This is because the UI plugin might register listeners into core services, 
	 * and this ensures that the UI plugin gets all updates, because they will only start after this. 
	 */
	public final void initializeAfterUIStart() {
		if(initializedAfterUI == true) {
			LangCore.logWarning("Atempted initializeAfterUIStart more than once.");
		} else {
			initializedAfterUI = true;
			
			langCore.startAgentsAfterUIStart(this);
		}
	}
	
	@Override
	public final void stop(BundleContext context) throws Exception {
		doCustomStop(context);
		
		langCore.shutdown();
		
		super.stop(context);
		pluginInstance = null;
	}
	
	protected abstract void doCustomStop(BundleContext context);
	
	
	/* ----------------- ----------------- */
	
	/** Creates an OK status with given message. */
	public static StatusExt createOkStatus(String message) {
		return createStatus(IStatus.OK, message, null);
	}
	
	/** Creates an Info status with given message. */
	public static StatusExt createInfoStatus(String message) {
		return createStatus(IStatus.INFO, message, null);
	}
	
	/** Creates a status describing an error in this plugin, with given message. */
	public static StatusExt createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}
	
	/** Creates a status describing an error in this plugin, with given message and given throwable. */
	public static StatusExt createErrorStatus(String message, Throwable throwable) {
		return createStatus(IStatus.ERROR, message, throwable);
	}
	
	/** Creates a Status with given status code and message. */
	public static StatusExt createStatus(int severity, String message, Throwable throwable) {
		return new StatusExt(severity, LangCore.getInstance(), message, throwable);
	}
	
	public static final class StatusExt extends Status {
		
		protected final Plugin plugin;
		
		public StatusExt(int severity, Plugin plugin, String message, Throwable exception) {
			super(severity, plugin.getBundle().getSymbolicName(), message, exception);
			this.plugin = plugin;
		}
		
		public void logInPlugin() {
			plugin.getLog().log(this);
		}
	}
	
	/** Creates a CoreException describing an error in this plugin. */
	public static CoreException createCoreException(String message, Throwable throwable) {
		return new CoreException(createErrorStatus(message, throwable));
	}
	
	/** Creates a CoreException describing an error in this plugin, from given {@link CommonException} */
	public static CoreException createCoreException(CommonException ce) {
		return createCoreException(ce.getMessage(), ce.getCause());
	}
	
	public static CommonException createCommonException(CoreException ce) {
		return new CommonException(ce.getMessage(), ce.getCause());
	}
	
	/* ----------------- Logging ----------------- */
	
	/** Logs given status. */
	public static void logStatus(IStatus status) {
		getInstance().getLog().log(status);
	}
	
	/** Logs status of given CoreException. */
	public static void logStatus(CoreException ce) {
		getInstance().getLog().log(ce.getStatus());
	}
	
	/** Logs status of given StatusException. */
	public static void logStatusException(StatusException se) {
		int severity = EclipseUtils.statusLevelToEclipseSeverity(se);
		logStatus(createStatus(severity, se.getMessage(), se.getCause()));
	}
	
	/** Logs an error status with given message. */
	public static void logError(String message) {
		getInstance().getLog().log(createErrorStatus(message, null));
	}
	
	/** Logs an error status with given message and given throwable. */
	public static void logError(String message, Throwable throwable) {
		getInstance().getLog().log(createErrorStatus(message, throwable));
	}
	
	public static void logError(CommonException ce) {
		logError(ce.getMessage(), ce.getCause());
	}
	
	/** Logs a warning status with given message */
	public static void logWarning(String message) {
		getInstance().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, null));
	}
	
	/** Logs a warning status with given message and given throwable */
	public static void logWarning(String message, Throwable throwable) {
		getInstance().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, throwable));
	}
	
	public static void logInternalError(Throwable throwable) {
		logError("Internal Error!", throwable);
	}
	
	public static final ILogHandler LOG_HANDLER = new ILogHandler() {
		@Override
		public void logStatus(StatusException statusException) {
			LangCore.logStatusException(statusException);
		}
	};
}