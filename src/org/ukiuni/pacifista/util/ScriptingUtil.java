package org.ukiuni.pacifista.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.ukiuni.pacifista.Console;
import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.Runtime;
import org.ukiuni.pacifista.velocity.VelocityWrapper;

public class ScriptingUtil {
	public static void execFolder(File target, File templateDir, Map<String, String> parameters) throws ScriptException, IOException {
		File[] childlen = target.listFiles();
		for (File file : childlen) {
			if (file.isDirectory()) {
				execFolder(file, templateDir, parameters);
			} else if (file.isFile()) {
				execScript(file.getParentFile(), file.getName(), templateDir, parameters);
			}
		}
	}

	public static void execScript(File baseDir, String script, File templateDir, Map<String, String> parameters) throws ScriptException, IOException {
		if (script.endsWith(".js")) {
			execScript("JavaScript", baseDir, script, templateDir, parameters);
		} else if (script.endsWith(".rb")) {
			execScript("jruby", baseDir, script, templateDir, parameters);
		} else if (script.endsWith(".groovy")) {
			execScript("groovy", baseDir, script, templateDir, parameters);
		}
	}

	public static void execScript(String lang, File baseDir, String script, File templateDir, Map<String, String> parameters) throws ScriptException, IOException {
		if (null == parameters) {
			parameters = new HashMap<String, String>();
		}
		ScriptEngineManager scriptManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = scriptManager.getEngineByName(lang);
		scriptEngine.put("Remote", new RemoteFactory());
		scriptEngine.put("Template", new VelocityWrapper(templateDir));
		scriptEngine.put("console", new Console());
		scriptEngine.put("runtime", new Runtime(baseDir, templateDir, parameters));
		if (script.startsWith("http://") || script.startsWith("https://")) {
			URL url = new URL(script);
			URLConnection connection = url.openConnection();
			scriptEngine.put(ScriptEngine.FILENAME, url.getFile());
			scriptEngine.eval(new InputStreamReader(connection.getInputStream()));
		} else {
			File file = new File(baseDir, script);
			scriptEngine.put(ScriptEngine.FILENAME, file.getName());
			scriptEngine.eval(new FileReader(file));
		}
	}

	public static Map<String, String> pickupParameters(String url) {
		Map<String, String> map = new HashMap<String, String>();
		if (url.contains("?")) {
			String query = url.substring(url.indexOf("?") + 1);
			String[] parameterSets = query.split("&");
			for (int i = 0; i < parameterSets.length; i++) {
				int questIndex = parameterSets[i].indexOf("=");
				if (questIndex > 0) {
					String key = parameterSets[i].substring(0, questIndex);
					String value = parameterSets[i].substring(questIndex + 1);
					map.put(key, value);
				}
			}
		}
		return map;
	}
}
