package org.ukiuni.pacifista.virtual;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.ukiuni.pacifista.Local;
import org.ukiuni.pacifista.util.IOUtil;
import org.ukiuni.pacifista.util.StreamUtil;

public class VirtualBoxHost {

	private String host;
	private File baseDir;
	private static final int READ_BUFFER_SIZE = 1024;

	public VirtualBoxHost(String host, File baseDir) {
		this.host = host;
		this.baseDir = baseDir;
	}

	public void boot(String parameters) throws IOException, InterruptedException {
		Local.executeNowait(new String[] { "VBoxHeadless", "-startvm", host });
	}

	public void boot() throws IOException, InterruptedException {
		boot(null);
	}

	public void shutdown() throws IOException, InterruptedException {
		Local.execute(new String[] { "VBoxManage", "controlvm", host, "poweroff" });
	}

	public boolean isRunning() throws IOException, InterruptedException {
		String result = Local.execute(new String[] { "VBoxManage", "list", "runningvms" });
		if (result.contains("\"" + host + "\"")) {
			return true;
		}
		return false;
	}

	public boolean isExist() throws IOException, InterruptedException {
		try {
			Local.execute(new String[] { "VBoxManage", "showvminfo", host });
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public String downloadImage(String url) throws IOException {
		return downloadImage(url, null, 0);
	}

	public String downloadImage(String url, String proxyHost, int proxyPort) throws IOException {
		return downloadImage(url, null, 0, null, null);
	}

	public String downloadImage(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		InputStream in;
		if (null == proxyHost) {
			in = new URL(url).openConnection().getInputStream();
		} else {
			URLConnection connection = new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
			if (null != proxyUser && null != proxyPass) {
				String encoded = new String(Base64.encodeBase64(new String("username:password").getBytes()));
				connection.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
			}
			in = connection.getInputStream();
		}
		File vmdir = new File(new File(baseDir, "vmimages"), host);
		vmdir.mkdirs();
		String vmFileName = new File(new URL(url).getFile()).getName();
		File vmFile = new File(vmdir, vmFileName);
		FileOutputStream out = new FileOutputStream(vmFile);
		IOUtil.copy(in, out);
		in.close();
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

	public int create(String stragePath) throws IOException, InterruptedException {
		int port = new Random().nextInt(65535);
		create(stragePath, "RedHat_64", "512", String.valueOf(port));
		return port;
	}

	public void create(String stragePath, String type, String memory, String port) throws IOException, InterruptedException {
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
		Local.execute(new String[] { "VBoxManage", "modifyvm", host, "--memory", memory, "--nic1", "nat", "--nictype1", "82545EM" });
		Local.execute(new String[] { "VBoxManage", "storagectl", host, "--name", host + "sata1", "--add", "sata", "--bootable", "on" });
		Local.execute(new String[] { "VBoxManage", "-nologo", "internalcommands", "sethduuid", stragePath });
		Local.execute(new String[] { "VBoxManage", "storageattach", host, "--storagectl", host + "sata1", "--port", "0", "--device", "0", "--type", "hdd", "--medium", stragePath });
		Local.execute(new String[] { "VBoxManage", "controlvm", host, "natpf1", "SSH,tcp,," + port + ",,22" });

		String vagrantSettingFilePath = Local.find(new File(new File(baseDir, "vmimages"), host), "Vagrantfile");
		if (null != vagrantSettingFilePath) {
			Map<String, String> param = loadVagrantSetting(vagrantSettingFilePath);
			if (param.containsKey("config.vm.base_mac")) {
				Local.execute(new String[] { "VBoxManage", "modifyvm", host, "--macaddress1", param.get("config.vm.base_mac").replace("\"", "") });
			}
		}
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

	public void remove() throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(new String[] { "VBoxManage", "unregistervm", host, "--delete" });
		int returnCode = process.waitFor();
		if (0 != returnCode) {
			throw new RuntimeException("create failed returnCode = " + returnCode + ", " + StreamUtil.inputToString(process.getErrorStream()));
		}
	}
}
