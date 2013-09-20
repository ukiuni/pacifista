package org.ukiuni.pacifista;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.ukiuni.pacifista.util.IOUtil;
import org.ukiuni.pacifista.util.StreamUtil;

public class Local {
	private final File baseDir;
	private PrintStream out = System.out;

	public Local(File baseDir) {
		this.baseDir = baseDir;
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
		return download(url, encode, null, 0, null, null);
	}

	public String download(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return download(url, "UTF-8", null, 0, null, null);
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
		File file;
		if (path.startsWith("/")) {
			file = new File(path);
		} else {
			file = new File(baseDir, path);
		}
		FileOutputStream out = new FileOutputStream(file);
		Http.download(url, out);
		out.close();
	}
}
