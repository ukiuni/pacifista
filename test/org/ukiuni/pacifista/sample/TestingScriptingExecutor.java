package org.ukiuni.pacifista.sample;

import java.io.File;

import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.util.ScriptingUtil;

public class TestingScriptingExecutor {
	public static void main(String[] args) throws Exception {
		try {
			ScriptingUtil.execFolder(new File("."), new File("./testingScripts"), new File("template"), new File("plugins"), null);
		} finally {
			RemoteFactory.closeAll();
		}
	}
}
