package org.ukiuni.pacifista.virtual;

import java.io.File;

public class VirtualMachine {
	private File baseDir;

	public VirtualMachine(File baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * Specify host and load Host object.
	 * @param type "AWS" is Amazon Web Services host or other is VirtualBox
	 * @param host host nick name.
	 * @return
	 */
	public VirtualHost getHost(String type, String host) {
		if ("AWS".equals(type)) {
			return new EC2VirtualHost(baseDir, host);
		}
		return new VirtualBoxHost(baseDir, host);
	}

	/**
	 * Specify host and load VirtualBox Host object.
	 * @param host host nick name.
	 * @return
	 */
	public VirtualHost getHost(String host) {
		return new VirtualBoxHost(baseDir, host);
	}
}
