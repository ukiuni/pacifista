package test.org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.ukiuni.pacifista.PluginLoader;
import org.ukiuni.pacifista.PluginLoader.PluginDownloadInfo;

public class TestPluginLoader {
	@Test
	public void testDownloadFiles() throws IOException {
		PluginLoader pluginLoader = new PluginLoader();
		File testDataDir = new File("testData");
		pluginLoader.setPluginHostUrl("file://" + testDataDir.getAbsolutePath() + "/");
		Set<PluginLoader.PluginDownloadInfo> pluginDownloadInfos = pluginLoader.loadPluginInfo("pluginTest.xml", "v0.0.2", true, null, 0, null, null);
		Assert.assertEquals(3, pluginDownloadInfos.size());
	}

	@Test
	public void testDownloadFilesNotLoadsDependsOn() throws IOException {
		PluginLoader pluginLoader = new PluginLoader();
		File testDataDir = new File("testData");
		pluginLoader.setPluginHostUrl("file://" + testDataDir.getAbsolutePath() + "/");
		Set<PluginLoader.PluginDownloadInfo> pluginDownloadInfos = pluginLoader.loadPluginInfo("pluginTest.xml", "v0.0.2", false, null, 0, null, null);
		Assert.assertEquals(1, pluginDownloadInfos.size());
	}

	@Test
	public void testDownloadPlugin() throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		PluginLoader pluginLoader = new PluginLoader();
		File testDataDir = new File("testData");
		pluginLoader.setPluginHostUrl("file://" + testDataDir.getAbsolutePath() + "/");
		pluginLoader.downloadPluginIfNotHave(new File("."), pluginLoader.loadAllPluginFromDirectory(new File("plugins")), "pluginTest.xml", "v0.0.2", null, 0, null, null);
		Assert.assertTrue(new File("testData/samplePlugin.jar").isFile());
		new File("testData/samplePlugin.jar").delete();
	}

	@Test
	public void testDownloadPluginAndDelete() throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		PluginLoader pluginLoader = new PluginLoader();
		File testDataDir = new File("testData");
		pluginLoader.setPluginHostUrl("file://" + testDataDir.getAbsolutePath() + "/");
		pluginLoader.downloadPluginIfNotHave(new File("."), pluginLoader.loadAllPluginFromDirectory(new File("plugins")), "pluginTest.xml", "v0.0.2", null, 0, null, null);
		Assert.assertTrue(new File("testData/commons-cli-1.2-bin.zip").isFile());
		Assert.assertTrue(new File("testData/commons-el-1.0.zip").isFile());
		Assert.assertTrue(new File("testData/commons-email-1.3.1-bin.zip").isFile());

		pluginLoader.deletePlugin(new File("."), "pluginTest.xml", "v0.0.2", null, 0, null, null);

		Assert.assertFalse(new File("testData/commons-cli-1.2-bin.zip").exists());
		Assert.assertTrue(new File("testData/commons-el-1.0.zip").exists());
		Assert.assertTrue(new File("testData/commons-email-1.3.1-bin.zip").exists());

		new File("testData/commons-el-1.0.zip").delete();
		new File("testData/commons-email-1.3.1-bin.zip").delete();
	}

	@Test
	public void testDownloadFromWeb() throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		PluginLoader pluginLoader = new PluginLoader();
		pluginLoader.downloadPluginIfNotHave(new File("."), pluginLoader.loadAllPluginFromDirectory(new File("plugins")), "python", null, null, 0, null, null);
		Assert.assertTrue(new File("plugins/jython-engine-.jar").isFile());
		Assert.assertTrue(new File("plugins/jython-2.5.3.jar").isFile());
		new File("plugins/jython-engine-.jar").delete();
		new File("plugins/jython-2.5.3.jar").delete();
	}

	@Test
	public void testLoadAllPluginInfo() throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		PluginLoader pluginLoader = new PluginLoader();
		pluginLoader.setPluginHostUrl("file://" + new File("testData/pluginsRepository/pluginList.xml").getAbsolutePath());
		List<PluginDownloadInfo> pluginDownloadInfos = pluginLoader.loadAllPluginInfos(null, 0, null, null);
		Assert.assertEquals(3, pluginDownloadInfos.size());
		pluginDownloadInfos.get(0).getName().equals("ruby");
		pluginDownloadInfos.get(1).getName().equals("groovy");
		pluginDownloadInfos.get(2).getName().equals("python");
		pluginDownloadInfos.get(0).getVersion().equals("1.9");
		pluginDownloadInfos.get(1).getVersion().equals("2.1.6");
		pluginDownloadInfos.get(2).getVersion().equals("2.5.3");
		pluginDownloadInfos.get(0).getDescription().equals("This plugin is able to write script with ruby");
		pluginDownloadInfos.get(1).getDescription().equals("This plugin is able to write script with groovy\nThis plugin is groovy its own. groovy is licensed under ASL2.0");
		pluginDownloadInfos.get(2).getDescription().equals("This plugin is able to write script with python\nThis plugin is jython its own. jython is licensed under The Jython License (http://www.jython.org/license.html)");
	}
}
