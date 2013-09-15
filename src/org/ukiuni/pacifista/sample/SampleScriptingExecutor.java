package org.ukiuni.pacifista.sample;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;

import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.util.ScriptingUtil;

public class SampleScriptingExecutor {
	public static void main(String[] args) throws ScriptException, IOException {
		try {
			ScriptingUtil.execScript(new File("."), "sampleScripts/setup.js", new File("templates"), null);
			RemoteFactory.closeAll();
			ScriptingUtil.execScript(new File("."), "sampleScripts/setup.rb", new File("templates"), null);
			RemoteFactory.closeAll();
			ScriptingUtil.execScript(new File("."), "sampleScripts/setup.groovy", new File("templates"), null);
			RemoteFactory.closeAll();
			ScriptingUtil.execScript(new File("."), "sampleScripts/updateVersion.js", new File("templates"), null);
		} finally {
			RemoteFactory.closeAll();
		}
	}
}
