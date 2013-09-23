package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.ukiuni.pacifista.util.ScriptingUtil;
import org.ukiuni.pacifista.util.ScriptingUtil.ScriptEngineNotFoundException;

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
		Map<String, String> parameters = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			if ("--parameters".equals(args[i])) {
				ScriptingUtil.parseParameters(parameters, args[i + 1]);
			}
			if ("--plugin".equals(args[i])) {
				PluginLoader pluginLoader = new PluginLoader();
				String version = null;
				if (args.length > i + 3) {
					version = args[i + 3];
				}
				String proxyHost = parameters.get("proxyHost");
				int proxyPort = 0;
				if (parameters.containsKey("proxyPort")) {
					proxyPort = Integer.parseInt(parameters.get("proxyPort"));
				}
				String proxyUser = parameters.get("proxyUser");
				String proxyPassword = parameters.get("proxyPassword");
				String pluginHost = parameters.get("pluginHost");
				if (null != pluginHost) {
					pluginLoader.setPluginHostUrl(pluginHost);
					System.out.println("set host " + pluginHost);
				}
				String printVersion = "";
				if (version != null) {
					printVersion = "version " + version + " ";
				}
				System.out.println("start " + args[i + 2] + " " + printVersion + args[i + 1]);

				if ("install".equals(args[i + 1])) {
					pluginLoader.downloadPluginIfNotHave(baseDir, pluginLoader.loadAllPluginFromDirectory(new File(baseDir, "plugins")), args[i + 2], version, proxyHost, proxyPort, proxyUser, proxyPassword);
				} else if ("delete".equals(args[i + 1])) {
					pluginLoader.deletePlugin(baseDir, args[i + 2], version, proxyHost, proxyPort, proxyUser, proxyPassword);
				}
				System.out.println("plugin " + args[i + 2] + " " + args[i + 1] + "ed");
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
		} catch (ScriptEngineNotFoundException e) {
			String lang = e.getScript();
			if ("JavaScript".equals(lang)) {
				System.out.println("This java is not contain JavaScript ScriptEngine. Use sun java. or add JavaScript ScriptEngine ");
			} else if ("jruby".equals(lang)) {
				System.out.println("install ruby plugin. execute, \"pacifista --plugin install ruby\"");
			} else if ("groovy".equals(lang)) {
				System.out.println("install ruby plugin. execute, \"pacifista --plugin install groovy\"");
			} else if ("python".equals(lang)) {
				System.out.println("install ruby plugin. execute, \"pacifista --plugin install python\"");
			}

		} finally {
			RemoteFactory.closeAll();
		}
	}
}
