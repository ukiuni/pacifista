package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

import javax.script.ScriptException;

import org.ukiuni.pacifista.util.ScriptingUtil;

public class Main {
	public static void main(String[] args) throws ScriptException, IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		String baseDirPath;
		if (0 == args.length) {
			baseDirPath = ".";
		} else {
			baseDirPath = args[1];
		}
		File baseDir = new File(baseDirPath);
		File templateDir = new File(baseDir, "templates");
		File pluginDir = new File(baseDir, "plugins");
		for (int i = 0; i < args.length; i++) {
			if ("--plugin".equals(args[i])) {
				PluginLoader pluginLoader = new PluginLoader();
				String version = null;
				if (args.length > i + 2) {
					version = args[i + 3];
				}
				Map<String, String> parameters;
				if (args.length > i + 3) {
					parameters = ScriptingUtil.parseParameters(args[i + 4]);
				} else {
					parameters = Collections.emptyMap();
				}
				String proxyHost = parameters.get("proxyHost");
				int proxyPort = 0;
				if (parameters.containsKey("proxyPort")) {
					proxyPort = Integer.parseInt(parameters.get("proxyPort"));
				}
				String proxyUser = parameters.get("proxyUser");
				String proxyPassword = parameters.get("proxyPassword");

				if ("install".equals(args[i + 1])) {
					pluginLoader.downloadPluginIfNotHave(baseDir, pluginLoader.loadAllPluginFromDirectory(new File(baseDir, "plugins")), args[i + 2], version, proxyHost, proxyPort, proxyUser, proxyPassword);
				} else if ("delete".equals(args[i + 1])) {
					pluginLoader.deletePlugin(baseDir, args[i + 2], version, proxyHost, proxyPort, proxyUser, proxyPassword);
				}
				return;
			}
		}
		try {
			if (args.length > 2) {
				for (int i = 2; i < args.length; i++) {
					ScriptingUtil.execScript(baseDir, args[i], templateDir, pluginDir, null);
				}
			} else {
				File scriptDir = new File(baseDir, "scripts");
				ScriptingUtil.execFolder(baseDir, scriptDir, templateDir, pluginDir, null);
			}
		} finally {
			RemoteFactory.closeAll();
		}
	}
}
