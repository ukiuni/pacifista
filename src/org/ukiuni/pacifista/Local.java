package org.ukiuni.pacifista;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ukiuni.pacifista.util.IOUtil;

public class Local {
	private final File baseDir;

	public Local(File baseDir) {
		this.baseDir = baseDir;
	}

	public void mkdir(String path) {
		new File(baseDir, path).mkdirs();
	}

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

	public void remove(String filePath) {
		File file = new File(baseDir, filePath);
		remove(file);
	}

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

	public String load(String filePath) throws IOException {
		return load(filePath, "UTF-8");
	}

	public String load(String filePath, String encode) throws IOException {
		File file = new File(baseDir, filePath);
		byte[] buffer = new byte[(int) file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(buffer);
		in.close();
		return new String(buffer, encode);
	}
}
