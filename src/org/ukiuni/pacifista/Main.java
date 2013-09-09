package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;

import org.ukiuni.pacifista.util.ScriptingUtil;

public class Main {
	public static void main(String[] args) throws ScriptException, IOException {
		String baseDirPath;
		if (0 == args.length) {
			baseDirPath = ".";
		} else {
			baseDirPath = args[1];
		}
		File baseDir = new File(baseDirPath);
		File templateDir = new File(baseDir, "template");
		try {
			if (args.length > 2) {
				for (int i = 2; i < args.length; i++) {
					ScriptingUtil.execScript(baseDir, args[i], templateDir, null);
				}
			} else {
				File scriptDir = new File(baseDir, "scripts");
				ScriptingUtil.execFolder(baseDir, scriptDir, templateDir, null);
			}
		} finally {
			RemoteFactory.closeAll();
		}
	}
}
