package org.ukiuni.pacifista;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.UUID;
import java.util.regex.Pattern;

import org.ukiuni.pacifista.util.FileUtil;
import org.ukiuni.pacifista.util.IOUtil;
import org.ukiuni.pacifista.util.StreamUtil;
import org.ukiuni.pacifista.util.TarUtil;
import org.ukiuni.pacifista.util.ZipUtil;

public class Local {
	private final File baseDir;
	private PrintStream out = System.out;
	private Runtime runtime;

	public Local(File baseDir, Runtime runtime) {
		this.baseDir = baseDir;
		this.runtime = runtime;
	}

	/**
	 * make directory
	 * 
	 * @param path
	 */
	public void mkdir(String path) {
		new File(baseDir, path).mkdirs();
	}

	/**
	 * save value as file.
	 * 
	 * @param filePath
	 * @param value
	 * @throws IOException
	 */
	public void save(String filePath, String value) throws IOException {
		save(filePath, value, "UTF-8");
	}

	public void save(String filePath, String value, String encode) throws IOException {
		File file = new File(baseDir, filePath);
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		out.write(value.getBytes(encode));
		out.close();
	}

	/**
	 * copy file.
	 * 
	 * @param fromFilePath
	 * @param toFilePath
	 * @throws IOException
	 */
	public void copy(String fromFilePath, String toFilePath) throws IOException {
		File fromFile = new File(baseDir, fromFilePath);
		File toFile = new File(baseDir, toFilePath);
		toFile.getParentFile().mkdirs();
		FileInputStream in = new FileInputStream(fromFile);
		FileOutputStream out = new FileOutputStream(toFile);
		IOUtil.copy(in, out);
		in.close();
		out.close();
	}

	/**
	 * delete file.
	 * 
	 * @param filePath
	 */
	public void remove(String filePath) {
		File file = new File(baseDir, filePath);
		remove(file);
	}

	/**
	 * delete file.
	 * 
	 * @param file
	 */
	public void remove(File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				remove(child);
			}
			file.delete();
		}
	}

	/**
	 * Load file as String.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public String load(String filePath) throws IOException {
		return load(filePath, "UTF-8");
	}

	/**
	 * Load file as String.
	 * 
	 * @param filePath
	 * @param encode
	 * @return
	 * @throws IOException
	 */
	public String load(String filePath, String encode) throws IOException {
		File file = new File(baseDir, filePath);
		byte[] buffer = new byte[(int) file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(buffer);
		in.close();
		return new String(buffer, encode);
	}

	/**
	 * Execute command.
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String execute(String command) throws IOException, InterruptedException {
		return execute(command.split(" "));
	}

	/**
	 * Execute command and output result.
	 * 
	 * @param command
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void call(String command) throws IOException, InterruptedException {
		out.print(execute(command));
	}

	/**
	 * Execute command.
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String execute(String[] command) throws IOException, InterruptedException {
		Process process = java.lang.Runtime.getRuntime().exec(command);
		int returnCode = process.waitFor();
		if (0 != returnCode) {
			throw new RuntimeException(concat(command, " ") + " failed returnCode = " + returnCode + ", " + StreamUtil.inputToString(process.getErrorStream()));
		}
		String returnMessage = StreamUtil.inputToString(process.getInputStream());
		process.destroy();
		return returnMessage;
	}

	/**
	 * Execute command with no block.
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Process executeNowait(String command) throws IOException, InterruptedException {
		return java.lang.Runtime.getRuntime().exec(command.split(" "));
	}

	/**
	 * Execute command with no block.
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Process executeNowait(String[] command) throws IOException, InterruptedException {
		return java.lang.Runtime.getRuntime().exec(command);
	}

	private static String concat(String[] args, String bond) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i]);
			if (args.length - 1 != i) {
				buffer.append(bond);
			}
		}
		return buffer.toString();
	}

	/**
	 * Find file
	 * 
	 * @param path
	 * @param fileName
	 * @return absolute file path or null when not find.
	 */
	public static String find(String path, String fileName) {
		try {
			return find(new File(path), fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Find file
	 * 
	 * @param path
	 * @param fileName
	 * @return absolute file path or null when not find.
	 */
	public static String find(File file, String fileName) {
		if (file.isFile()) {
			if (file.getName().equals(fileName)) {
				return file.getAbsolutePath();
			}
		} else if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (null != children) {
				for (File child : children) {
					String result = find(child, fileName);
					if (null != result) {
						return result;
					}
				}
			}
		}
		return null;
	}

	public PrintStream getOut() {
		return out;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public String download(String url) throws IOException {
		return download(url, "UTF-8");
	}

	public String download(String url, String encode) throws IOException {
		String proxyHost = (String) runtime.getEnv("httpProxyHost");
		int proxyPort = null == runtime.getEnv("httpProxyPort") ? 0 : Integer.parseInt((String) runtime.getEnv("httpProxyPort"));
		String proxyUser = (String) runtime.getEnv("httpProxyUser");
		String proxyPassword = (String) runtime.getEnv("httpProxyPassword");
		return download(url, encode, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String download(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return download(url, "UTF-8", proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public String download(String url, String encode, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Http.download(url, out);
		return new String(out.toByteArray(), encode);
	}

	public void downloadAsFile(String url, String path) throws IOException {
		downloadAsFile(url, path, null, 0, null, null);
	}

	public void downloadAsFile(String url, String path, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		File file = FileUtil.pathToFile(baseDir, path);
		FileOutputStream out = new FileOutputStream(file);
		Http.download(url, out);
		out.close();
	}

	public void decompress(String filePath, String outDirectoryPath) throws IOException {
		if (filePath.endsWith(".tar.gz") || filePath.endsWith(".tgz")) {
			unTarGz(filePath, outDirectoryPath);
		} else if (filePath.endsWith(".tar.bz2") || filePath.endsWith(".tbz")) {
			unTarBz2(filePath, outDirectoryPath);
		} else if (filePath.endsWith(".zip")) {
			unzip(filePath, outDirectoryPath);
		} else {
			throw new IOException("unknown archive format " + filePath);
		}
	}

	public void unzip(String zipFilePath, String outDirectoryPath) throws IOException {
		File zipFile = FileUtil.pathToFile(baseDir, zipFilePath);
		File outDirectory = FileUtil.pathToFile(baseDir, outDirectoryPath);
		outDirectory.mkdirs();
		ZipUtil.unzip(zipFile, outDirectory);
	}

	public void unTarGz(String tarGzFilePath, String outDirectoryPath) throws IOException {
		File tarGzFile = FileUtil.pathToFile(baseDir, tarGzFilePath);
		File outDirectory = FileUtil.pathToFile(baseDir, outDirectoryPath);
		outDirectory.mkdirs();
		TarUtil.unTarGz(tarGzFile, outDirectory);
	}

	public void unTarBz2(String tarGzFilePath, String outDirectoryPath) throws IOException {
		File tarGzFile = FileUtil.pathToFile(baseDir, tarGzFilePath);
		File outDirectory = FileUtil.pathToFile(baseDir, outDirectoryPath);
		outDirectory.mkdirs();
		TarUtil.unTarBz2(tarGzFile, outDirectory);
	}

	public void unTar(String tarGzFilePath, String outDirectoryPath) throws IOException {
		File tarGzFile = FileUtil.pathToFile(baseDir, tarGzFilePath);
		File outDirectory = FileUtil.pathToFile(baseDir, outDirectoryPath);
		outDirectory.mkdirs();
		TarUtil.unTar(tarGzFile, outDirectory);
	}

	public void zip(String directoryPath, String zipFilePath) throws IOException {
		File directory = FileUtil.pathToFile(baseDir, directoryPath);
		File zipFile = FileUtil.pathToFile(baseDir, zipFilePath);
		OutputStream out = null;
		try {
			out = new FileOutputStream(zipFile);
			ZipUtil.zip(directory, out);
			out.close();
		} catch (Exception e) {
			IOUtil.close(out);
			zipFile.delete();
		}
	}

	public void comment(String filePath, String replaceLine) throws FileNotFoundException, IOException {
		File file = FileUtil.pathToFile(baseDir, filePath);
		comment(file, replaceLine);
	}

	public void comment(File file, String replaceLine) throws FileNotFoundException, IOException {
		comment(file, replaceLine, "UTF-8");
	}

	public void comment(String filePath, final String replaceLine, String encode) throws FileNotFoundException, IOException {
		File file = FileUtil.pathToFile(baseDir, filePath);
		comment(file, replaceLine, encode);
	}

	public void comment(File file, final String replaceLine, String encode) throws FileNotFoundException, IOException {
		final Pattern pattern = Pattern.compile(replaceLine);
		ReplaceCallback replaceCallback = new ReplaceCallback() {
			@Override
			public String replaceTo(String org) {
				if (pattern.matcher(org).matches()) {
					return "#" + org;
				} else {
					return org;
				}
			}
		};
		replaceLine(file, replaceCallback, encode);
	}

	public void uncomment(String filePath, String replaceLine) throws FileNotFoundException, IOException {
		File file = FileUtil.pathToFile(baseDir, filePath);
		uncomment(file, replaceLine);

	}

	public void uncomment(File file, String replaceLine) throws FileNotFoundException, IOException {
		comment(file, replaceLine, "UTF-8");
	}

	public void uncomment(String filePath, final String replaceLine, String encode) throws FileNotFoundException, IOException {
		File file = FileUtil.pathToFile(baseDir, filePath);
		comment(file, replaceLine, encode);
	}

	public void uncomment(File file, final String replaceLine, String encode) throws FileNotFoundException, IOException {
		final Pattern pattern = Pattern.compile(replaceLine);
		ReplaceCallback replaceCallback = new ReplaceCallback() {
			@Override
			public String replaceTo(String org) {
				if (org.startsWith("#") && org.length() >= 2 && pattern.matcher(org.substring(1)).matches()) {
					return org.substring(1);
				} else {
					return org;
				}
			}
		};
		replaceLine(file, replaceCallback, encode);
	}

	public void replaceLine(String filePath, String replaceFrom, String replaceTo) throws IOException, FileNotFoundException {
		replaceLine(filePath, replaceFrom, replaceTo, "UTF-8");
	}

	public void replaceLine(String filePath, String replaceFrom, String replaceTo, String encode) throws IOException, FileNotFoundException {
		File file = FileUtil.pathToFile(baseDir, filePath);
		replaceLine(file, replaceFrom, replaceTo, encode);
	}

	public void replaceLine(File file, String replaceFrom, final String replaceTo, String encode) throws IOException, FileNotFoundException {
		final Pattern pattern = Pattern.compile(replaceFrom);
		ReplaceCallback replaceCallback = new ReplaceCallback() {
			@Override
			public String replaceTo(String org) {
				if (pattern.matcher(org).matches()) {
					return replaceTo;
				} else {
					return org;
				}
			}
		};
		replaceLine(file, replaceCallback, encode);
	}

	private static interface ReplaceCallback {
		public String replaceTo(String org);
	}

	public void replaceLine(File file, ReplaceCallback replaceCallback, String encode) throws IOException, FileNotFoundException {
		String tmpFileName = UUID.randomUUID().toString();
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
		String line = in.readLine();
		PrintStream out = new PrintStream(new FileOutputStream(tmpFileName), false, encode);

		while (null != line) {
			line = replaceCallback.replaceTo(line);

			out.println(line);
			line = in.readLine();
		}
		out.close();
		in.close();
		file.delete();
		new File(tmpFileName).renameTo(file);
	}
}
