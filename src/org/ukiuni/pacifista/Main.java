package org.ukiuni.pacifista;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, ScriptException {
		if (args.length > 2) {
			for (int i = 2; i < args.length; i++) {
				execScript(args[i]);
			}
		} else {
			File baseDir = new File("script");
			execFolder(baseDir);
		}
	}

	public static void execFolder(File target) throws FileNotFoundException, ScriptException {
		if (target.isDirectory()) {
			execFolder(target);
		} else if (target.isFile()) {
			execScript(target.getPath());
		}
	}

	public static void execScript(String script) throws FileNotFoundException, ScriptException {
		if (script.endsWith(".js")) {
			execScript("JavaScript", script);
		} else if (script.endsWith(".rb")) {
			execScript("jruby", script);
		} else if (script.endsWith(".groovy")) {
			execScript("groovy", script);
		}
	}

	public static void execScript(String lang, String script) throws FileNotFoundException, ScriptException {
		ScriptEngineManager grManager = new ScriptEngineManager();
		ScriptEngine grEngine = grManager.getEngineByName(lang);
		grEngine.put("Remote", new RemoteFactory());
		grEngine.eval(new FileReader(script));
	}
}
