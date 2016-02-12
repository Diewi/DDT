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
package melnorme.lang.utils;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import melnorme.lang.tooling.ToolingMessages;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;

public class ProcessUtils {
	
	public static ProcessBuilder createProcessBuilder(List<String> commandLine, Location workingDir) {
		File file = workingDir == null ? null : workingDir.toFile();
		return createProcessBuilder(commandLine, file); 
	}
	
	public static ProcessBuilder createProcessBuilder(List<String> commandLine, File workingDir) {
		assertTrue(commandLine.size() > 0);
		
		ProcessBuilder pb = new ProcessBuilder(commandLine);
		if(workingDir != null) {
			pb.directory(workingDir);
		}
		return pb;
	}
	
	public static ProcessBuilder createProcessBuilder(Path cmdExePath, Location workingDir, String... arguments) {
		return createProcessBuilder(cmdExePath, workingDir, false, arguments);
	}
	
	public static ProcessBuilder createProcessBuilder(Path cmdExePath, Location workingDir, 
			boolean addCmdDirToPath, String... arguments) {
		
		ArrayList2<String> commandLine = new ArrayList2<>();
		commandLine.add(cmdExePath.toString());
		commandLine.addElements(arguments);
		ProcessBuilder pb = createProcessBuilder(commandLine, workingDir);
		
		if(addCmdDirToPath) {
			EnvUtils.addDirToPathEnv(cmdExePath, pb);
		}
		
		return pb;
	}
	
	/* -----------------  ----------------- */
	
	public static void validateNonZeroExitValue(int exitValue) throws CommonException {
		if(exitValue != 0) {
			throw new CommonException(ToolingMessages.PROCESS_CompletedWithNonZeroValue("Process", exitValue));
		}
	}
	
}