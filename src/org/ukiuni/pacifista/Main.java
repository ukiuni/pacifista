package org.ukiuni.pacifista;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.ukiuni.pacifista.velocity.VelocityWrapper;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, ScriptException {
		String baseDirPath;
		if (0 == args.length) {
			baseDirPath = ".";
		} else {
			baseDirPath = args[1];
		}
		File baseDir = new File(baseDirPath);
		File templateDir = new File(baseDir, "template");
		if (args.length > 2) {
			for (int i = 2; i < args.length; i++) {
				execScript(args[i], templateDir);
			}
		} else {
			File scriptDir = new File(baseDir, "scripts");
			execFolder(scriptDir, templateDir);
		}
	}

	public static void execFolder(File target, File templateDir) throws FileNotFoundException, ScriptException {
		File[] childlen = target.listFiles();
		for (File file : childlen) {
			if (file.isDirectory()) {
				execFolder(file, templateDir);
			} else if (file.isFile()) {
				execScript(file.getPath(), templateDir);
			}
		}
	}

	public static void execScript(String script, File templateDir) throws FileNotFoundException, ScriptException {
		if (script.endsWith(".js")) {
			execScript("JavaScript", script, templateDir);
		} else if (script.endsWith(".rb")) {
			execScript("jruby", script, templateDir);
		} else if (script.endsWith(".groovy")) {
			execScript("groovy", script, templateDir);
		}
	}

	public static void execScript(String lang, String script, File templateDir) throws FileNotFoundException, ScriptException {
		ScriptEngineManager grManager = new ScriptEngineManager();
		ScriptEngine grEngine = grManager.getEngineByName(lang);
		grEngine.put("Remote", new RemoteFactory());
		grEngine.put("Template", new VelocityWrapper(templateDir));
		grEngine.put("console", new Console());
		grEngine.put("env", new Enviroment());
		grEngine.eval(new FileReader(script));
	}
}
