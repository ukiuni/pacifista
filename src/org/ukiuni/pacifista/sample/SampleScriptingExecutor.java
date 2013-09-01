package org.ukiuni.pacifista.sample;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;

import org.ukiuni.pacifista.util.ScriptingUtil;

public class SampleScriptingExecutor {
	public static void main(String[] args) throws ScriptException, IOException {
		ScriptingUtil.execScript(new File("."), "sampleScripts/setup.rb", new File("template"), null);
		ScriptingUtil.execScript(new File("."), "sampleScripts/setup.js", new File("template"), null);
		ScriptingUtil.execScript(new File("."), "sampleScripts/setup.groovy", new File("template"), null);
	}
}
