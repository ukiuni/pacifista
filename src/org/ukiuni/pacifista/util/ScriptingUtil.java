package org.ukiuni.pacifista.util;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.ukiuni.pacifista.Http;
import org.ukiuni.pacifista.Local;
import org.ukiuni.pacifista.PluginLoader;
import org.ukiuni.pacifista.PluginLoader.Plugin;
import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.Runtime;
import org.ukiuni.pacifista.Tester;
import org.ukiuni.pacifista.velocity.VelocityWrapper;
import org.ukiuni.pacifista.virtual.VirtualMachine;

public class ScriptingUtil {
	public static void execFolder(File baseDir, File target, File templateDir, File pluginDir, Map<String, Object> parameters) throws ScriptException, IOException, ScriptEngineNotFoundException {
		if (!target.exists()) {
			throw new FileNotFoundException(target.getAbsolutePath());
		}
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
				execFolder(baseDir, file, templateDir, pluginDir, parameters);
			} else if (file.isFile()) {
				String canonical = picupCanonical(baseDir, file);
				execScript(baseDir, canonical, templateDir, pluginDir, parameters);
			}
		}
	}

	public static String picupCanonical(File baseDir, File file) {
		String baseAbsolute = baseDir.getAbsolutePath();
		String fileAbsolute = file.getAbsolutePath();
		int compaireToLength = baseAbsolute.length() < fileAbsolute.length() ? baseAbsolute.length() : fileAbsolute.length();
		String canonical = null;
		for (int i = 0; i <= compaireToLength; i++) {
			if (!baseAbsolute.startsWith(fileAbsolute.substring(0, i))) {
				break;
			}
		}
		if (null == canonical) {
			canonical = fileAbsolute.substring(baseAbsolute.length() - 1);
		}
		return canonical;
	}

	public static void execScript(File baseDir, String script, File templateDir, File pluginDir, Map<String, Object> parameters) throws ScriptException, IOException, ScriptEngineNotFoundException {
		if (script.endsWith(".js")) {
			execScript("JavaScript", baseDir, script, templateDir, pluginDir, parameters);
		} else if (script.endsWith(".rb")) {
			execScript("jruby", baseDir, script, templateDir, pluginDir, parameters);
		} else if (script.endsWith(".groovy")) {
			execScript("groovy", baseDir, script, templateDir, pluginDir, parameters);
		} else if (script.endsWith(".py")) {
			execScript("python", baseDir, script, templateDir, pluginDir, parameters);
		}
	}

	public static void execScript(String lang, File baseDir, String script, File templateDir, File pluginDir, Map<String, Object> parameters) throws ScriptException, IOException, ScriptEngineNotFoundException {
		if (null == parameters) {
			parameters = new HashMap<String, Object>();
		}
		ScriptEngineManager scriptManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = scriptManager.getEngineByName(lang);
		if (null == scriptEngine) {
			throw new ScriptEngineNotFoundException(lang);
		}
		Runtime runtime = new Runtime(baseDir, templateDir, pluginDir, parameters);
		scriptEngine.put("Remote", new RemoteFactory(baseDir, runtime));
		scriptEngine.put("Template", new VelocityWrapper(templateDir));
		scriptEngine.put("console", new Console());
		scriptEngine.put("runtime", runtime);
		scriptEngine.put("Tester", new Tester());
		Local local = new Local(baseDir, runtime);
		scriptEngine.put("local", local);
		scriptEngine.put("http", new Http(local));
		scriptEngine.put("git", new Git(baseDir, runtime));
		scriptEngine.put("VirtualMachine", new VirtualMachine(baseDir));
		scriptEngine.put("VirtualMacine", new VirtualMachine(baseDir));

		if (null != pluginDir && pluginDir.isDirectory()) {
			for (File pluginJar : pluginDir.listFiles()) {
				if (pluginJar.getName().equals("README")) {
					continue;
				}
				try {
					List<Plugin> plugins = new PluginLoader().loadPlugin(pluginJar);
					for (Plugin plugin : plugins) {
						scriptEngine.put(plugin.getName(), plugin.getInstance());
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		if (script.startsWith("http://") || script.startsWith("https://")) {
			URL url = new URL(script);
			URLConnection connection = url.openConnection();
			scriptEngine.put(ScriptEngine.FILENAME, script);
			scriptEngine.eval(new InputStreamReader(connection.getInputStream()));
		} else if (script.startsWith("/")) {
			File file = new File(script);
			scriptEngine.put(ScriptEngine.FILENAME, file.getName());
			scriptEngine.eval(new FileReader(file));
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
			parseParameters(map, query);
		}
		return map;
	}

	public static void parseParameters(Map<String, String> map, String query) {
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

	public static Map<String, String> parseParameters(String query) {
		Map<String, String> map = new HashMap<String, String>();
		parseParameters(map, query);
		return map;
	}

	public static LsResult parseLs(String lsResultString) throws ParseException {
		LsResult lsResult;
		try {
			String[] sprited = lsResultString.split(" ");
			lsResult = new LsResult();
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
		} catch (Throwable e) {
			throw new RuntimeException("ls String \"" + lsResultString + "\" is not parseable.", e);
		}
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

	@SuppressWarnings("serial")
	public static class ScriptEngineNotFoundException extends Exception {
		public String script;

		public ScriptEngineNotFoundException(String script) {
			this.script = script;
		}

		public String getScript() {
			return script;
		}

		public void setScript(String script) {
			this.script = script;
		}

	}
}
