package org.ukiuni.pacifista;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PluginLoader {
	private static final String PACIFISTA_PLUGIN_ATTRIBUTE_NAME = "PacifistaPluginName";
	private static final String PACIFISTA_PLUGIN_ATTRIBUTE_CLASS = "PacifistaPluginClass";
	private static final String PACIFISTA_PLUGIN_ATTRIBUTE_VERSION = "PacifistaPluginVirsion";
	private static final String PACIFISTA_PLUGIN_ATTRIBUTE_DESCRIPTION = "PacifistaPluginDescription";
	private static final String PACIFISTA_PLUGIN_ATTRIBUTE_PLUGINSDEPENDSON = "PacifistaPluginDependsOn";
	private static final String PACIFISTA_PLUGIN_HOST_URL = "http://pacifista.ukiuni.org/plugins/xml/";
	private String pluginHostUrl = PACIFISTA_PLUGIN_HOST_URL;

	public void downloadPluginIfNotHave(File baseDir, List<Plugin> aleadyExistsPlugin, String name, String version, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		Set<PluginDownloadInfo> loadPluginInfos = loadPluginInfo(name, version, true, proxyHost, proxyPort, proxyUser, proxyPassword);
		for (PluginDownloadInfo pluginDownloadInfo : loadPluginInfos) {
			if (!hasAleady(aleadyExistsPlugin, pluginDownloadInfo)) {
				for (DownloadFile downloadFile : pluginDownloadInfo.getDownloadFiles()) {
					FileOutputStream out = new FileOutputStream(new File(baseDir, downloadFile.downloadTo));
					Http.download(downloadFile.downloadFrom, out, proxyHost, proxyPort, proxyUser, proxyPassword);
					out.close();
				}
			}
		}
	}

	public void deletePlugin(File baseDir, String name, String version, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		Set<PluginDownloadInfo> loadPluginInfos = loadPluginInfo(name, version, false, proxyHost, proxyPort, proxyUser, proxyPassword);
		for (PluginDownloadInfo pluginDownloadInfo : loadPluginInfos) {
			for (DownloadFile downloadFile : pluginDownloadInfo.getDownloadFiles()) {
				new File(baseDir, downloadFile.downloadTo).delete();
			}

		}
	}

	private boolean hasAleady(List<Plugin> plugins, PluginDownloadInfo pluginDownloadInfo) {
		for (Plugin plugin : plugins) {
			if (plugin.equals(pluginDownloadInfo.name)) {
				if (plugin.version == null && pluginDownloadInfo.version == null) {
					return true;
				} else if (pluginDownloadInfo.version != null && pluginDownloadInfo.version.equals(plugin.version)) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<PluginDownloadInfo> loadPluginInfo(String name, String version, boolean loadDependsOn, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		Set<PluginDownloadInfo> pluginDownloadInfos = new HashSet<PluginDownloadInfo>();
		loadPluginInfo(name, version, pluginDownloadInfos, loadDependsOn, proxyHost, proxyPort, proxyUser, proxyPassword);
		return pluginDownloadInfos;
	}

	public void loadPluginInfo(String name, String version, Set<PluginDownloadInfo> pluginDownloadInfos, boolean loadDependsOn, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		String urlPath = getPluginHostUrl() + name;
		if (null != version) {
			urlPath = urlPath + "?version=" + version;
		}
		loadPluginInfo(urlPath, pluginDownloadInfos, loadDependsOn, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public void loadPluginInfo(String urlPath, Set<PluginDownloadInfo> pluginDownloadInfos, boolean loadDependsOn, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		URLConnection connection = Http.openConnection(urlPath, proxyHost, proxyPort, proxyUser, proxyPassword);
		if (connection instanceof HttpURLConnection && 300 <= ((HttpURLConnection) connection).getResponseCode()) {
			throw new IOException("response code is " + ((HttpURLConnection) connection).getResponseCode());
		}
		InputStream in = connection.getInputStream();
		loadPluginInfo(in, pluginDownloadInfos, loadDependsOn, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public void loadPluginInfo(InputStream in, Set<PluginDownloadInfo> pluginDownloadInfos, boolean loadDependsOn, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		SAXParserFactory spfactory = SAXParserFactory.newInstance();
		PluginHandler handler = new PluginHandler();
		try {
			SAXParser parser = spfactory.newSAXParser();
			parser.parse(in, handler);
		} catch (Exception e) {
			throw new IOException(e);
		}
		in.close();
		PluginDownloadInfo pluginDownloadInfo = handler.getPluginDownloadInfo();
		pluginDownloadInfos.add(pluginDownloadInfo);

		for (Plugin dependsOnPlugin : pluginDownloadInfo.getDependsOns()) {
			if (loadDependsOn && !isAleadyHasDependency(pluginDownloadInfos, dependsOnPlugin)) {
				loadPluginInfo(dependsOnPlugin.name, dependsOnPlugin.version, pluginDownloadInfos, loadDependsOn, proxyHost, proxyPort, proxyUser, proxyPassword);
			}
		}
	}

	private boolean isAleadyHasDependency(Set<PluginDownloadInfo> pluginDownloadInfos, Plugin plugin) {
		for (PluginDownloadInfo dependsPluginDownloadInfo : pluginDownloadInfos) {
			if (plugin.name.equals(dependsPluginDownloadInfo.name)) {
				if (plugin.version == null) {
					return true;
				}
				if (plugin.version.equals(dependsPluginDownloadInfo.version)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Plugin> loadAllPluginFromDirectory(File directory) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		List<Plugin> plugins = new ArrayList<PluginLoader.Plugin>();
		File[] jarFiles = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".jar") || file.getName().endsWith(".zip");
			}
		});
		for (File file : jarFiles) {
			plugins.addAll(loadPlugin(file));
		}
		return plugins;
	}

	public List<Plugin> loadPlugin(File file) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		JarFile jarFile = new JarFile(file);
		Manifest manifest = jarFile.getManifest();
		if (null == manifest) {
			return Collections.emptyList();
		}
		Attributes attributes = manifest.getMainAttributes();
		Pattern pattern = Pattern.compile("^" + PACIFISTA_PLUGIN_ATTRIBUTE_NAME + "([0-9]+)$");
		List<Plugin> plugins = new ArrayList<Plugin>();
		for (Object key : attributes.keySet()) {
			Matcher matcher = pattern.matcher(key.toString());
			if (matcher.find()) {
				String number = matcher.group(1);
				String className = attributes.getValue(PACIFISTA_PLUGIN_ATTRIBUTE_CLASS + number);
				if (null != className) {
					Plugin plugin = new Plugin();
					plugin.setName(attributes.getValue(key.toString()));
					plugin.setInstance(Class.forName(className.trim()).getConstructor().newInstance());
					plugins.add(plugin);
					String version = attributes.getValue(PACIFISTA_PLUGIN_ATTRIBUTE_VERSION + number);
					plugin.setVersion(version);
					String description = attributes.getValue(PACIFISTA_PLUGIN_ATTRIBUTE_DESCRIPTION + number);
					plugin.setDescription(description);
					String pluginsDependsOnConcated = attributes.getValue(PACIFISTA_PLUGIN_ATTRIBUTE_PLUGINSDEPENDSON + number);
					if (null != pluginsDependsOnConcated) {
						String[] pluginsDependsOn = splitAndTrim(pluginsDependsOnConcated);
						plugin.setPluginsDependsOn(pluginsDependsOn);
					}
				}
			}
		}
		return plugins;
	}

	private String[] splitAndTrim(String src) {
		String[] pluginsDependsOn = src.split(",");
		for (int i = 0; i < pluginsDependsOn.length; i++) {
			pluginsDependsOn[i] = pluginsDependsOn[i].trim();
		}
		return pluginsDependsOn;
	}

	public String getPluginHostUrl() {
		return pluginHostUrl;
	}

	public void setPluginHostUrl(String pluginHostUrl) {
		this.pluginHostUrl = pluginHostUrl;
	}

	public static class Plugin {
		private String name;
		private Object instance;
		private String version;
		private String description;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String[] getPluginsDependsOn() {
			return pluginsDependsOn;
		}

		public void setPluginsDependsOn(String[] pluginsDependsOn) {
			this.pluginsDependsOn = pluginsDependsOn;
		}

		private String[] pluginsDependsOn;

		public Object getInstance() {
			return instance;
		}

		public void setInstance(Object instance) {
			this.instance = instance;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private class PluginHandler extends DefaultHandler {
		private String currentValue;
		private PluginDownloadInfo downloadInfo;
		private DownloadFile currentDownloadFile;
		private Plugin currentPlugin;
		private boolean inPlugin;

		@Override
		public void startDocument() throws SAXException {
			downloadInfo = new PluginDownloadInfo();
			downloadInfo.downloadFiles = new ArrayList<DownloadFile>();
			downloadInfo.dependsOns = new ArrayList<Plugin>();
		}

		@Override
		public void characters(char[] ch, int offset, int length) {
			currentValue = new String(ch, offset, length).trim();
		}

		@Override
		public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
			if (qName.equals("downloadFile")) {
				currentDownloadFile = new DownloadFile();
			} else if (qName.equals("dependsOnPlugin")) {
				currentPlugin = new Plugin();
				inPlugin = true;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (inPlugin) {
				if (qName.equals("name")) {
					currentPlugin.setName(currentValue);
				} else if (qName.equals("version")) {
					currentPlugin.setVersion(currentValue);
				} else if (qName.equals("dependsOnPlugin")) {
					downloadInfo.dependsOns.add(currentPlugin);
					currentPlugin = null;
					inPlugin = false;
				}
			} else if (qName.equals("name")) {
				downloadInfo.name = currentValue;
			} else if (qName.equals("version")) {
				downloadInfo.version = currentValue;
			} else if (qName.equals("from")) {
				currentDownloadFile.downloadFrom = currentValue;
			} else if (qName.equals("to")) {
				currentDownloadFile.downloadTo = currentValue;
			} else if (qName.equals("downloadFile")) {
				downloadInfo.downloadFiles.add(currentDownloadFile);
				currentDownloadFile = null;
			}
		}

		public PluginDownloadInfo getPluginDownloadInfo() {
			return downloadInfo;
		}
	}

	public class PluginDownloadInfo {
		public String name;
		public String version;
		public List<DownloadFile> downloadFiles;
		public List<Plugin> dependsOns;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public List<DownloadFile> getDownloadFiles() {
			return downloadFiles;
		}

		public void setDownloadFiles(List<DownloadFile> downloadFiles) {
			this.downloadFiles = downloadFiles;
		}

		public List<Plugin> getDependsOns() {
			return dependsOns;
		}

		public void setDependsOns(List<Plugin> dependsOns) {
			this.dependsOns = dependsOns;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PluginDownloadInfo) {
				PluginDownloadInfo pluginDownloadInfo = (PluginDownloadInfo) obj;
				if (this.name.equals(pluginDownloadInfo.name)) {
					if (this.version == null && pluginDownloadInfo.version == null) {
						return true;
					} else if (this.version != null && this.version.equals(pluginDownloadInfo.version)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (this.name + ":" + this.version).hashCode();
		}
	}

	public class DownloadFile {
		public String downloadFrom;
		public String downloadTo;
	}
}
