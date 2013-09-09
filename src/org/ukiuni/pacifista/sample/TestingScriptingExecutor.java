package org.ukiuni.pacifista.sample;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;

import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.util.ScriptingUtil;

public class TestingScriptingExecutor {
	public static void main(String[] args) throws ScriptException, IOException {
		try {
			ScriptingUtil.execFolder(new File("."), new File("./testingScripts"), new File("template"), null);
		} finally {
			RemoteFactory.closeAll();
		}
	}
}
