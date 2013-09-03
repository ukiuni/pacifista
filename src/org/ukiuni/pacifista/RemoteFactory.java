package org.ukiuni.pacifista;

import java.util.ArrayList;
import java.util.List;

public class RemoteFactory {
	private static List<Remote> remotes = new ArrayList<Remote>();

	public Remote create() {
		Remote remote = new Remote();
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
