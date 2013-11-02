package org.ukiuni.pacifista.virtual;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.ukiuni.pacifista.Local;
import org.ukiuni.pacifista.util.HttpUtil;
import org.ukiuni.pacifista.util.ScriptingUtil;
import org.ukiuni.pacifista.util.StreamUtil;

public class VirtualBoxHost implements VirtualHost {

	private String host;
	private File baseDir;
	private static final int READ_BUFFER_SIZE = 1024;
	private Map<String, String> parameterMap = new HashMap<String, String>();

	public VirtualBoxHost(File baseDir, String host) {
		this.baseDir = baseDir;
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ukiuni.pacifista.virtual.VirtualHost#setParameters(java.lang.String)
	 */
	@Override
	public void setParameters(String parameters) {
		ScriptingUtil.parseParameters(parameterMap, parameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ukiuni.pacifista.virtual.VirtualHost#boot()
	 */
	@Override
	public void boot() throws IOException, InterruptedException {
		Local.executeNowait(new String[] { "VBoxHeadless", "-startvm", host });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ukiuni.pacifista.virtual.VirtualHost#shutdown()
	 */
	@Override
	public void shutdown() throws IOException, InterruptedException {
		Local.execute(new String[] { "VBoxManage", "controlvm", host, "poweroff" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ukiuni.pacifista.virtual.VirtualHost#isRunning()
	 */
	@Override
	public boolean isRunning() throws IOException, InterruptedException {
		String result = Local.execute(new String[] { "VBoxManage", "list", "runningvms" });
		if (result.contains("\"" + host + "\"")) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ukiuni.pacifista.virtual.VirtualHost#isExist()
	 */
	@Override
	public boolean isExist() throws IOException, InterruptedException {
		try {
			String line = Local.execute(new String[] { "VBoxManage", "showvminfo", host });
			return !line.contains("Could not find a registered machine");
		} catch (Throwable e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ukiuni.pacifista.virtual.VirtualHost#downloadImage(java.lang.String)
	 */
	@Override
	public String downloadImage(String url) throws IOException {
		return downloadImage(url, null, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ukiuni.pacifista.virtual.VirtualHost#downloadImage(java.lang.String,
	 * java.lang.String, int)
	 */
	@Override
	public String downloadImage(String url, String proxyHost, int proxyPort) throws IOException {
		return downloadImage(url, null, 0, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ukiuni.pacifista.virtual.VirtualHost#downloadImage(java.lang.String,
	 * java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public String downloadImage(String url, String proxyHost, int proxyPort, final String proxyUser, final String proxyPass) throws IOException {
		File vmdir = new File(new File(baseDir, "vmimages"), host);
		vmdir.mkdirs();
		String vmFileName = new File(new URL(url).getFile()).getName();
		File vmFile = new File(vmdir, vmFileName);
		FileOutputStream out = new FileOutputStream(vmFile);
		HttpUtil.download(url, out, proxyHost, proxyPort, proxyUser, proxyPass);
		out.close();
		if (vmFile.getName().endsWith(".box")) {
			TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(vmFile));
			File tarDir = new File(vmdir, vmFileName.substring(0, vmFileName.length() - 4));
			tarDir.mkdirs();
			File returnFile = null;
			TarArchiveEntry entry = tarIn.getNextTarEntry();
			while (null != entry) {
				long leftSize = entry.getSize();
				byte[] buffer = new byte[READ_BUFFER_SIZE];
				File fileInTarFile = new File(tarDir, entry.getName());
				FileOutputStream tarOut = new FileOutputStream(fileInTarFile);
				while (leftSize > 0) {
					int readSize = (int) (READ_BUFFER_SIZE < leftSize ? READ_BUFFER_SIZE : leftSize);
					if (READ_BUFFER_SIZE != readSize) {
						buffer = new byte[readSize];
					}
					int readed = tarIn.read(buffer, 0, readSize);
					tarOut.write(buffer, 0, readed);
					leftSize -= readSize;
				}
				tarOut.close();
				if (fileInTarFile.getName().endsWith(".vmdk")) {
					returnFile = fileInTarFile;
				}
				entry = tarIn.getNextTarEntry();
			}
			tarIn.close();
			return returnFile.getAbsolutePath();
		} else {
			return vmFile.getAbsolutePath();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ukiuni.pacifista.virtual.VirtualHost#create(java.lang.String)
	 */
	@Override
	public InstanceSSHAddress create(String stragePath) throws IOException, InterruptedException {
		int port = new Random().nextInt(65535);
		create(stragePath, "RedHat_64", 512, port);
		return new InstanceSSHAddress("localhost", port, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ukiuni.pacifista.virtual.VirtualHost#create(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public InstanceSSHAddress create(String stragePath, String type, int memory, int port) throws IOException, InterruptedException {
		if (!stragePath.startsWith("/")) {
			stragePath = new File(baseDir, stragePath).getAbsolutePath();
		}
		String returnValue = Local.execute(new String[] { "VBoxManage", "createvm", "--name", host, "--ostype", type });

		BufferedReader reader = new BufferedReader(new StringReader(returnValue));
		String vboxFile = null;
		String line = reader.readLine();
		while (null != line) {
			if (line.startsWith("Settings")) {
				Matcher matcher = Pattern.compile("'(.+?)'").matcher(line);
				matcher.find();
				vboxFile = matcher.group().replace("\'", "");
			}
			line = reader.readLine();
		}

		Local.execute(new String[] { "VBoxManage", "registervm", vboxFile });
		Local.execute(new String[] { "VBoxManage", "modifyvm", host, "--memory", String.valueOf(memory), "--nic1", "nat", "--nictype1", "82545EM" });
		Local.execute(new String[] { "VBoxManage", "storagectl", host, "--name", host + "sata1", "--add", "sata", "--bootable", "on" });
		Local.execute(new String[] { "VBoxManage", "-nologo", "internalcommands", "sethduuid", stragePath });
		Local.execute(new String[] { "VBoxManage", "storageattach", host, "--storagectl", host + "sata1", "--port", "0", "--device", "0", "--type", "hdd", "--medium", stragePath });
		openPort("tcp", port, 22);
		String vagrantSettingFilePath = Local.find(new File(new File(baseDir, "vmimages"), host), "Vagrantfile");
		if (null != vagrantSettingFilePath) {
			Map<String, String> param = loadVagrantSetting(vagrantSettingFilePath);
			if (param.containsKey("config.vm.base_mac")) {
				Local.execute(new String[] { "VBoxManage", "modifyvm", host, "--macaddress1", param.get("config.vm.base_mac").replace("\"", "") });
			}
		}
		return new InstanceSSHAddress("localhost", port, null);
	}

	private Map<String, String> loadVagrantSetting(String vagrantSettingFilePath) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(vagrantSettingFilePath));
		String line = reader.readLine();
		while (line != null) {
			if (line.contains("=")) {
				int equalIndex = line.indexOf("=");
				param.put(line.substring(0, equalIndex - 1).trim(), line.substring(equalIndex + 1).trim());
			}
			line = reader.readLine();
		}
		reader.close();
		return param;
	}

	@Override
	public void openPort(String protocol, int port) throws IOException, InterruptedException {
		openPort(protocol, port, port);
	}

	public String openPort(String protocol, int localPort, int hostPort) throws IOException, InterruptedException {
		return Local.execute(new String[] { "VBoxManage", "controlvm", host, "natpf1", "," + protocol + ",," + localPort + ",," + hostPort });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ukiuni.pacifista.virtual.VirtualHost#remove()
	 */
	@Override
	public void remove() throws IOException, InterruptedException {
		try {
			if (isRunning()) {
				shutdown();
			}
		} catch (Throwable e) {
			// do nothing
		}
		Process process = Runtime.getRuntime().exec(new String[] { "VBoxManage", "unregistervm", host, "--delete" });
		int returnCode = process.waitFor();
		if (0 != returnCode) {
			throw new RuntimeException("create failed returnCode = " + returnCode + ", " + StreamUtil.inputToString(process.getErrorStream()));
		}
	}
}
