package org.ukiuni.pacifista.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public static void unzip(File srcFile, File saveDirectory) throws IOException {
		FileInputStream in = new FileInputStream(srcFile);
		unzip(in, saveDirectory);
	}

	public static void unzip(InputStream in, File saveDirectory) throws IOException {
		ZipInputStream zipIn = new ZipInputStream(in);
		ZipEntry ze = zipIn.getNextEntry();
		while (null != ze) {
			File outFile = new File(saveDirectory, ze.getName());
			if (ze.isDirectory()) {
				outFile.mkdirs();
			} else {
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(outFile));
					IOUtil.copy(zipIn, bos);
				} finally {
					IOUtil.close(bos);
				}
			}
			ze = zipIn.getNextEntry();
		}
		IOUtil.close(zipIn);
	}

	public static void zip(File srcDir, OutputStream out) throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(out);
		zip(srcDir.getParentFile(), srcDir, zipOut);
	}

	public static void zip(File rootDir, File src, ZipOutputStream zipOut) throws IOException {
		if (src.isFile()) {
			FileInputStream in = new FileInputStream(src);
			ZipEntry ze = new ZipEntry(src.getAbsolutePath().substring(rootDir.getAbsolutePath().length() + 1));
			zipOut.putNextEntry(ze);
			IOUtil.copy(in, zipOut);
			zipOut.closeEntry();
		} else if (src.isDirectory()) {
			for (File file : src.listFiles()) {
				zip(rootDir, file, zipOut);
			}
		}
	}
}
