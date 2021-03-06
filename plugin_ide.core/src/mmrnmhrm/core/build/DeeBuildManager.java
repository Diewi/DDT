/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.build;

import java.nio.file.Path;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import dtool.dub.DubBuildOutputParser;
import melnorme.lang.ide.core.EclipseCore;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ToolMarkersHelper;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.operations.build.BuildTarget;
import melnorme.lang.ide.core.operations.build.BuildTargetOperation;
import melnorme.lang.ide.core.operations.build.BuildTargetOperation.BuildOperationParameters;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.bundle.BundleInfo;
import melnorme.lang.tooling.common.SourceLineColumnRange;
import melnorme.lang.tooling.common.ToolSourceMessage;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.PathUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import melnorme.utilbox.status.Severity;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.DeeBundleModel;
import mmrnmhrm.core.engine.DeeToolManager;

public class DeeBuildManager extends BuildManager {
	
	public static final String BuildType_Default = "";
	
	public DeeBuildManager(DeeBundleModel bundleModel, DeeToolManager toolManager) {
		super(bundleModel, toolManager);
	}
	
	@Override
	public DeeToolManager getToolManager() {
		return (DeeToolManager) super.getToolManager();
	}
	
	@Override
	protected void bundleProjectAddedOrModified(IProject project, BundleInfo newBundleInfo) {
		if(newBundleInfo.getBundleDesc().isResolved()) {
			// We ignore resolved description, because only unresolved ones have configuration info
			return;
		}
		super.bundleProjectAddedOrModified(project, newBundleInfo);
	}
	
	@Override
	protected Indexable<BuildType> getBuildTypes_do() {
		return ArrayList2.<BuildType>create(
			new DeeBuildType(BuildType_Default),
			new DeeBuildType(DubBuildType.UNITTEST.getBuildTypeString())
		);
	}
	
	/* -----------------  ----------------- */
	
	protected class DeeBuildType extends BuildType {
		
		public DeeBuildType(String name) {
			super(name);
		}
		
		@Override
		public String getDefaultCommandArguments(BuildTarget bt) throws CommonException {
			ArrayList2<String> buildArgs = new ArrayList2<>();
			
			String buildConfigName = bt.getBuildConfigName();
			String dubBuildType = bt.getBuildTypeName();
			
			buildArgs.add("build");
			
			if(!buildConfigName.equals(BundleInfo.DEFAULT_CONFIGURATION)) {
				buildArgs.addElements("-c" , buildConfigName);
			}
			
			if(!dubBuildType.isEmpty()) {
				buildArgs.addElements("-b" , dubBuildType);
			}
			
			return StringUtil.collToString(buildArgs, " ");
		}
		
		@Override
		public BuildTargetOperation getBuildOperation(BuildOperationParameters buildOpParams) throws CommonException {
			return new DeeBuildTargetOperation(buildOpParams);
		}
		
	}
	
	public class DeeBuildTargetOperation extends BuildTargetOperation {
		
		public DeeBuildTargetOperation(BuildOperationParameters buildOpParams) {
			super(buildOpParams);
		}
		
		@Override
		protected void processBuildOutput(ExternalProcessResult processResult, IOperationMonitor om) 
				throws CommonException {
			new DubBuildOutputParser<CommonException>() {
				@Override
				protected void processDubFailure(String dubErrorLine) throws CommonException {
					try {
						addDubFailureMarker(dubErrorLine);
					} catch(CoreException e) {
						throw EclipseUtils.createCommonException(e);
					}
				};
				
				@Override
				protected void processCompilerError(String filePathStr, String startPosStr, String errorMsg) {
					Path filePath;
					try {
						filePath = PathUtil.createPath(filePathStr);
					} catch(CommonException e) {
						LangCore.logError("Invalid path for tool message: ", e);
						return;
					}
					try {
						addCompilerErrorMarker(filePath, startPosStr, errorMsg);
					} catch (CoreException e) {
						// log, but otherwise ignore & continue
						EclipseCore.logStatus(e);
					}
				}
			}.handleResult(processResult);
		}
		
		public void addDubFailureMarker(String dubErrorLine) throws CoreException {
			String errorMessage = 
					dubErrorLine == null ? DeeCoreMessages.RunningDubBuild_Error : dubErrorLine;
			
			IMarker dubMarker = getProject().createMarker(DeeBundleModelManager.DUB_PROBLEM_ID);
			dubMarker.setAttribute(IMarker.MESSAGE, errorMessage);
			dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		}
		
		protected void addCompilerErrorMarker(Path filePath, String startPosStr, String errorMsg) 
				throws CoreException {
			int line = -1;
			int column = -1;
			int endLine = -1;
			int endColumn = -1;
			
			try {
				String lineStr = StringUtil.substringUntilMatch(startPosStr, ",");
				String columnStr = StringUtil.segmentAfterMatch(startPosStr, ",");
				line = Integer.valueOf(lineStr);
				column = Integer.valueOf(columnStr);
			} catch (NumberFormatException e) {
				
			}
			
			SourceLineColumnRange sourceLinePos = new SourceLineColumnRange(line, column, endLine, endColumn);
			ToolSourceMessage toolMessage = new ToolSourceMessage(filePath, sourceLinePos, Severity.ERROR, errorMsg);
			
			Location projectLocation;
			try {
				projectLocation = ResourceUtils.getProjectLocation2(project);
			} catch(CommonException e) {
				throw EclipseCore.createCoreException(e);
			}
			new ToolMarkersHelper(true).addErrorMarkers(toolMessage, projectLocation);
		}
		
	}
	
}