package org.ukiuni.pacifista.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.ukiuni.pacifista.Console;
import org.ukiuni.pacifista.Git;
import org.ukiuni.pacifista.Local;
import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.Runtime;
import org.ukiuni.pacifista.Tester;
import org.ukiuni.pacifista.velocity.VelocityWrapper;

public class ScriptingUtil {
	public static void execFolder(File target, File templateDir, Map<String, Object> parameters) throws ScriptException, IOException {
		File[] childlen = target.listFiles();
		List<File> sortedList = new ArrayList<File>();
		for (File file : childlen) {
			sortedList.add(file);
		}
		Collections.sort(sortedList, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				if (f1.isDirectory()) {
					if (f2.isDirectory()) {
						return f1.compareTo(f2);
					} else {
						return 1;
					}
				} else {
					if (f2.isDirectory()) {
						return -1;
					} else {
						return f1.compareTo(f2);
					}
				}
			}
		});
		for (File file : sortedList) {
			if (file.isDirectory()) {
				execFolder(file, templateDir, parameters);
			} else if (file.isFile()) {
				execScript(file.getParentFile(), file.getName(), templateDir, parameters);
			}
		}
	}

	public static void execScript(File baseDir, String script, File templateDir, Map<String, Object> parameters) throws ScriptException, IOException {
		if (script.endsWith(".js")) {
			execScript("JavaScript", baseDir, script, templateDir, parameters);
		} else if (script.endsWith(".rb")) {
			execScript("jruby", baseDir, script, templateDir, parameters);
		} else if (script.endsWith(".groovy")) {
			execScript("groovy", baseDir, script, templateDir, parameters);
		}
	}

	public static void execScript(String lang, File baseDir, String script, File templateDir, Map<String, Object> parameters) throws ScriptException, IOException {
		if (null == parameters) {
			parameters = new HashMap<String, Object>();
		}
		ScriptEngineManager scriptManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = scriptManager.getEngineByName(lang);
		scriptEngine.put("Remote", new RemoteFactory());
		scriptEngine.put("Template", new VelocityWrapper(templateDir));
		scriptEngine.put("console", new Console());
		scriptEngine.put("runtime", new Runtime(baseDir, templateDir, parameters));
		scriptEngine.put("Tester", new Tester());
		scriptEngine.put("local", new Local(baseDir));
		scriptEngine.put("git", new Git(baseDir));
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

	public static LsResult parseLs(String lsResultString) throws ParseException {
		String[] sprited = lsResultString.split(" ");
		LsResult lsResult = new LsResult();
		lsResult.isDir = sprited[0].startsWith("d");
		lsResult.mode = sprited[0];
		if (lsResult.mode.endsWith(".")) {
			lsResult.mode = lsResult.mode.substring(1, lsResult.mode.length() - 1);
		} else {
			lsResult.mode = lsResult.mode.substring(1);
		}
		lsResult.group = sprited[2];
		lsResult.owner = sprited[3];
		lsResult.size = Long.parseLong(sprited[4]);
		lsResult.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(sprited[5] + " " + sprited[6]);
		lsResult.name = sprited[7];
		return lsResult;
	}

	public static class LsResult {
		public boolean isDir;
		public String mode;
		public String group;
		public String owner;
		public Date date;
		public long size;
		public String name;
	}
}
