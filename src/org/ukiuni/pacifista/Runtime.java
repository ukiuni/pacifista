package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.ukiuni.pacifista.util.ScriptingUtil;

public class Runtime {
	private Map<String, String> dataMap = new HashMap<String, String>();
	private final File templateDir;
	private File baseDir;

	public Runtime(File baseDir, File templateDir, Map<String, String> dataMap) {
		this.baseDir = baseDir;
		this.templateDir = templateDir;
		this.dataMap = dataMap;
	}

	public String getEnv(String key) {
		if (dataMap.containsKey(key)) {
			return dataMap.get(key);
		}
		return System.getenv(key);
	}

	public void setEnv(String key, String value) {
		dataMap.put(key, value);
	}

	public String get_env(String key) {
		return getEnv(key);
	}

	public void set_env(String key, String value) {
		setEnv(key, value);
	}

	public void call(String script) throws ScriptException, IOException {
		Map<String, String> hashMap = new HashMap<String, String>(this.dataMap);
		if (script.contains("?")) {
			hashMap.putAll(ScriptingUtil.pickupParameters(script));
			script = script.substring(0, script.indexOf("?"));
		}
		ScriptingUtil.execScript(this.baseDir, script, this.templateDir, hashMap);
	}
}
