package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.ukiuni.pacifista.PluginLoader.PluginDownloadInfo;
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
		int parameterIndex = 0;
		Command command = new Command();
		for (int i = 0; i < args.length; i++) {
			if ("--parameters".equals(args[i])) {
				ScriptingUtil.parseParameters(parameters, args[i + 1]);
				parameterIndex = i;
			}
			if ("--plugin".equals(args[i])) {
				command.operation = Command.Operation.PLUGIN;
				if (!(args.length > i + 2 || (args.length > i + 1 && "list".equals(args[i + 1])))) {
					System.out.println("usage: pacifista --plugin [install|uninstall|list] {pluginName} {version} \nversion is option. other are needed.");
					return;
				}
				command.command = args[i + 1];
				if (args.length > i + 2) {
					command.pluginName = args[i + 2];
				}
				if (args.length > i + 3) {
					command.pluginVersion = args[i + 3];
				}
			}
		}
		if (Command.Operation.PLUGIN == command.operation) {
			String proxyHost = parameters.get("proxyHost");
			int proxyPort = 0;
			if (parameters.containsKey("proxyPort")) {
				proxyPort = Integer.parseInt(parameters.get("proxyPort"));
			}
			String proxyUser = parameters.get("proxyUser");
			String proxyPassword = parameters.get("proxyPassword");
			String pluginHost = parameters.get("pluginHost");

			PluginLoader pluginLoader = new PluginLoader();
			if (null != pluginHost) {
				pluginLoader.setPluginHostUrl(pluginHost);
				System.out.println("set host " + pluginHost);
			}

			if ("list".equals(command.command)) {
				List<PluginDownloadInfo> pluginDownloadInfos = pluginLoader.loadAllPluginInfos(proxyHost, proxyPort, proxyUser, proxyPassword);
				System.out.println("plugins /////////////");
				for (PluginDownloadInfo pluginDownloadInfo : pluginDownloadInfos) {
					System.out.println(pluginDownloadInfo.getName() + " : " + pluginDownloadInfo.getVersion());
					if (null != pluginDownloadInfo.getDescription()) {
						System.out.println("\t" + pluginDownloadInfo.getDescription().replace("\n", "\n\t"));
					}
					System.out.println();
				}
				System.out.println("/////////////////////");
				return;
			}

			String printVersion = "";
			if (command.pluginVersion != null) {
				printVersion = "version " + command.pluginVersion + " ";
			}

			System.out.println("start " + command.pluginName + " " + printVersion + command.command);

			if ("install".equals(command.command)) {
				pluginLoader.downloadPluginIfNotHave(baseDir, pluginLoader.loadAllPluginFromDirectory(new File(baseDir, "plugins")), command.pluginName, command.pluginVersion, proxyHost, proxyPort, proxyUser, proxyPassword);
			} else if ("uninstall".equals(command.command)) {
				pluginLoader.deletePlugin(baseDir, command.pluginName, command.pluginVersion, proxyHost, proxyPort, proxyUser, proxyPassword);
			} else {
				System.out.println("pacifista --plugin command must specify [install|uninstall] ");
			}
			System.out.println("plugin " + command.pluginName + " " + command.command + "ed");
			return;
		}
		Map<String, Object> env = new HashMap<String, Object>();
		for (String key : parameters.keySet()) {
			env.put(key, parameters.get(key));
		}
		try {
			if (parameterIndex + 2 < args.length) {
				for (int i = 2; i < args.length; i++) {
					ScriptingUtil.execScript(baseDir, args[i], templateDir, pluginDir, env);
				}
			} else {
				File scriptDir = new File(baseDir, "scripts");
				ScriptingUtil.execFolder(baseDir, scriptDir, templateDir, pluginDir, env);
			}
		} catch (ScriptEngineNotFoundException e) {
			String lang = e.getScript();
			if ("JavaScript".equals(lang)) {
				System.out.println("This java is not contain JavaScript ScriptEngine. Use sun java. or add JavaScript ScriptEngine ");
			} else if ("jruby".equals(lang)) {
				System.out.println("Install ruby plugin. execute, \"pacifista --plugin install ruby\"");
			} else if ("groovy".equals(lang)) {
				System.out.println("Install groovy plugin. execute, \"pacifista --plugin install groovy\"");
			} else if ("python".equals(lang)) {
				System.out.println("Install python plugin. execute, \"pacifista --plugin install python\"");
			}
		} finally {
			RemoteFactory.closeAll();
		}
	}

	private static class Command {
		public static enum Operation {
			PLUGIN, EXCEC;
		}

		public Operation operation = Operation.EXCEC;
		public String command;
		public String pluginName;
		public String pluginVersion;
	}
}
