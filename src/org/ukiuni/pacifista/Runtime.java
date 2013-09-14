package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.ukiuni.pacifista.util.ScriptingUtil;

public class Runtime {
	private static final Map<String, Object> dataMap = new HashMap<String, Object>();
	private final File templateDir;
	private File baseDir;

	public Runtime(File baseDir, File templateDir, Map<String, Object> dataMap) {
		this.baseDir = baseDir;
		this.templateDir = templateDir;
		Runtime.dataMap.putAll(dataMap);
	}

	public Object getEnv(String key) {
		if (dataMap.containsKey(key)) {
			return dataMap.get(key);
		}
		return System.getenv(key);
	}

	public void setEnv(String key, Object value) {
		dataMap.put(key, value);
	}

	public Object get_env(String key) {
		return getEnv(key);
	}

	public void set_env(String key, Object value) {
		setEnv(key, value);
	}

	public void call(String script) throws ScriptException, IOException {
		Map<String, Object> hashMap = new HashMap<String, Object>(Runtime.dataMap);
		if (script.contains("?")) {
			hashMap.putAll(ScriptingUtil.pickupParameters(script));
			script = script.substring(0, script.indexOf("?"));
		}
		ScriptingUtil.execScript(this.baseDir, script, this.templateDir, hashMap);
	}

	public void sleep(long wait) throws InterruptedException {
		Thread.sleep(wait);
	}
}
