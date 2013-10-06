package org.ukiuni.pacifista.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarUtil {
	public static void unTarGz(File tarGzFile, File destDirectory) throws IOException {
		InputStream tarGzIn = new FileInputStream(tarGzFile);
		unTarGz(tarGzIn, destDirectory);
	}

	public static void unTarBz2(File tarGzFile, File destDirectory) throws IOException {
		InputStream tarBz2In = new FileInputStream(tarGzFile);
		unTarBz2(tarBz2In, destDirectory);
	}

	public static void unTar(File tarFile, File destDirectory) throws IOException {
		InputStream in = new FileInputStream(tarFile);
		TarArchiveInputStream tarIn = new TarArchiveInputStream(in);
		unTar(tarIn, destDirectory);
	}

	public static void unTarGz(InputStream tarGzIn, File destDirectory) throws IOException {
		GZIPInputStream gzipCompressorInputStream = new GZIPInputStream(tarGzIn);
		TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipCompressorInputStream);
		unTar(tarIn, destDirectory);
	}

	public static void unTarBz2(InputStream tarBz2In, File destDirectory) throws IOException {
		BZip2CompressorInputStream bz2CompressorInputStream = new BZip2CompressorInputStream(tarBz2In);
		TarArchiveInputStream tarIn = new TarArchiveInputStream(bz2CompressorInputStream);
		unTar(tarIn, destDirectory);
	}

	public static void unTar(TarArchiveInputStream tarIn, File destDirectory) throws IOException {
		for (TarArchiveEntry entry; (entry = tarIn.getNextTarEntry()) != null;) {

			File entryFile = new File(destDirectory, entry.getName());
			if (entry.isDirectory()) {
				entryFile.mkdirs();
			} else {
				if (!entryFile.getParentFile().exists()) {
					entryFile.getParentFile().mkdirs();
				}
				OutputStream out = new FileOutputStream(entryFile);
				IOUtils.copy(tarIn, out);
				out.close();
			}
		}
	}
}
