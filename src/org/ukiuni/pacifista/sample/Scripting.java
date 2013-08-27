package org.ukiuni.pacifista.sample;

import java.io.FileNotFoundException;

import javax.script.ScriptException;

import org.ukiuni.pacifista.Main;

public class Scripting {
	public static void main(String[] args) throws FileNotFoundException, ScriptException {
		Main.execScript("jruby", "sampleScripts/setup.rb");
		Main.execScript("JavaScript", "sampleScripts/setup.js");
		Main.execScript("groovy", "sampleScripts/setup.groovy");
	}
}
