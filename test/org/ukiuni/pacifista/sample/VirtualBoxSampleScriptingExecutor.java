package org.ukiuni.pacifista.sample;

import java.io.File;

import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.util.ScriptingUtil;

public class VirtualBoxSampleScriptingExecutor {
	public static void main(String[] args) throws Exception {
		try {
			ScriptingUtil.execScript(new File("."), "sampleScripts/setupVirtualBox.js", new File("templates"), new File("plugins"), null);
		} finally {
			RemoteFactory.closeAll();
		}
	}
}
