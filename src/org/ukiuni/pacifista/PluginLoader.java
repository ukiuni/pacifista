package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginLoader {
	private String PACIFISTA_PLUGIN_ATTRIBUTE_KEY = "PacifistaPluginKey";
	private String PACIFISTA_PLUGIN_ATTRIBUTE_Class = "PacifistaPluginClass";

	public List<Plugin> loadPlugin(File file) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		JarFile jarFile = new JarFile(file);
		Manifest manifest = jarFile.getManifest();
		if (null == manifest) {
			return Collections.emptyList();
		}
		Attributes attributes = manifest.getMainAttributes();
		Pattern pattern = Pattern.compile("^" + PACIFISTA_PLUGIN_ATTRIBUTE_KEY + "([0-9]+)$");
		List<Plugin> plugins = new ArrayList<Plugin>();
		for (Object key : attributes.keySet()) {
			Matcher matcher = pattern.matcher(key.toString());
			if (matcher.find()) {
				String number = matcher.group(1);
				String className = attributes.getValue(PACIFISTA_PLUGIN_ATTRIBUTE_Class + number);
				if (null != className) {
					Plugin plugin = new Plugin();
					plugin.setKey(attributes.getValue(key.toString()));
					plugin.setInstance(Class.forName(className.trim()).getConstructor().newInstance());
					plugins.add(plugin);
				}
			}
		}
		return plugins;
	}

	public static class Plugin {
		private String key;
		private Object instance;

		public Object getInstance() {
			return instance;
		}

		public void setInstance(Object instance) {
			this.instance = instance;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}
}
