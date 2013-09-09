package org.ukiuni.pacifista;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RemoteFactory {
	private static List<Remote> remotes = new ArrayList<Remote>();
	private File baseDir;

	public RemoteFactory(File baseDir) {
		this.baseDir = baseDir;
	}

	public Remote create() {
		Remote remote = new Remote(baseDir);
		remotes.add(remote);
		return remote;
	}

	public static void closeAll() {
		for (Remote remote : remotes) {
			try {
				remote.close();
			} catch (Throwable e) {
			}
		}
	}
}
