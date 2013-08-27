package org.ukiuni.pacifista.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	private static final int BUFFER_SIZE = 1024;

	public static void copy(InputStream in, OutputStream out) throws IOException {
		copy(in, out, Long.MAX_VALUE);
	}

	public static void copy(InputStream in, OutputStream out, long fileSize) throws IOException {
		int toReadSize = (int) (BUFFER_SIZE < fileSize ? BUFFER_SIZE : fileSize);
		byte[] buffer = new byte[toReadSize];
		long readedSize = 0;
		for (int readed; (readed = in.read(buffer)) > 0 && readedSize < fileSize;) {
			out.write(buffer, 0, readed);
			readedSize += readed;
			if ((readedSize + BUFFER_SIZE) > fileSize) {
				buffer = new byte[(int) (fileSize - readedSize)];
			}
		}
	}
}
