package org.ukiuni.pacifista.sample;

import java.io.File;
import java.io.FileNotFoundException;

import javax.script.ScriptException;

import org.ukiuni.pacifista.Main;

public class Scripting {
	public static void main(String[] args) throws FileNotFoundException, ScriptException {
		Main.execScript("jruby", "sampleScripts/setup.rb", new File("template"));
		Main.execScript("JavaScript", "sampleScripts/setup.js", new File("template"));
		Main.execScript("groovy", "sampleScripts/setup.groovy", new File("template"));
	}
}
