package org.ukiuni.pacifista;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class for script. If you use java, use Remote class directry.
 * 
 * @author tito
 * 
 */
public class RemoteFactory {
	private static List<Remote> remotes = new ArrayList<Remote>();
	private File baseDir;

	/**
	 * Constructor, for pacifista platform.
	 * 
	 * @param baseDir
	 *            execute path
	 */
	public RemoteFactory(File baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * Create Remote object for script.
	 * 
	 * @return
	 */
	public Remote create() {
		Remote remote = new Remote(baseDir);
		remotes.add(remote);
		return remote;
	}

	/**
	 * Create Remote object and connect with specified parameter.
	 * 
	 * @param host
	 * @param port
	 * @param account
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public Remote create(String host, int port, String account, String password) throws IOException {
		Remote remote = create();
		remote.connect(host, port, account, password);
		return remote;
	}

	/**
	 * Create Remote object and connect with specified parameter.
	 * 
	 * @param host
	 * @param port
	 * @param account
	 * @param authFilePath
	 * @return
	 * @throws IOException
	 */
	public Remote createWithAuthFile(String host, int port, String account, String authFilePath) throws IOException {
		Remote remote = create();
		remote.connectWithAuthFile(host, port, account, authFilePath);
		return remote;
	}

	/**
	 * Close all Remtote object created by RemoteFactory
	 */
	public static void closeAll() {
		for (Remote remote : remotes) {
			try {
				remote.close();
			} catch (Throwable e) {
			}
		}
	}
}
