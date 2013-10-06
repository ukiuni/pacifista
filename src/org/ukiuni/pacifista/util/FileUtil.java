package org.ukiuni.pacifista.util;

import java.io.File;

public class FileUtil {
	public static File pathToFile(File baseDir, String path) {
		File file;
		if (path.startsWith("/")) {
			file = new File(path);
		} else {
			file = new File(baseDir, path);
		}
		return file;
	}

	public static DirectoryPartAndFileName dividePathToParentDirectoryAndFileName(String path) {
		int lastSrashIndex = path.lastIndexOf("/");
		DirectoryPartAndFileName directoryPartAndFileName = new DirectoryPartAndFileName();
		if (path.equals("/")) {
			directoryPartAndFileName.setDirectoryPart(path);
		} else if (lastSrashIndex < 0) {
			directoryPartAndFileName.setFileName(path);
		} else {
			directoryPartAndFileName.setDirectoryPart(path.substring(0, lastSrashIndex));
			directoryPartAndFileName.setFileName(path.substring(lastSrashIndex + 1));
		}
		return directoryPartAndFileName;
	}

	public static class DirectoryPartAndFileName {
		private String directoryPart;
		private String fileName;

		public String getDirectoryPart() {
			return directoryPart;
		}

		public void setDirectoryPart(String directoryPart) {
			this.directoryPart = directoryPart;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}
}
