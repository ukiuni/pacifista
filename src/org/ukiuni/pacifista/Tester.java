package org.ukiuni.pacifista;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ukiuni.pacifista.util.ScriptingUtil;
import org.ukiuni.pacifista.util.ScriptingUtil.LsResult;

public class Tester {
	private Remote remote;

	public Tester() {

	}

	public Tester(Remote remote) {
		this.remote = remote;
	}

	public Tester create(Remote remote) {
		return new Tester(remote);
	}

	public void assertEquals(Object arg1, Object arg2) {
		if (null == arg1 && null == arg2) {
			return;
		}
		if (!arg1.equals(arg2)) {
			throw new AssertionError("[" + arg1 + "] != [" + arg2 + "]");
		}
	}

	public void assertFile(String path, String mode) throws IOException, AssertionError {
		assertFile(path, mode, null, null, null);
	}

	public void assertFile(String path, String mode, String owner) throws IOException, AssertionError {
		assertFile(path, mode, null, owner, null);
	}

	public void assertFileIsFile(String path) throws IOException, AssertionError {
		assertFile(path, null, null, null, false);
	}

	public void assertFileIsDirectory(String path) throws IOException, AssertionError {
		assertFile(path, null, null, null, true);
	}

	public void assertUserExists(String user) throws IOException {
		String result = remote.execute("cat /etc/passwd");
		BufferedReader in = new BufferedReader(new StringReader(result));
		String resultLine = in.readLine();
		while (null != resultLine) {
			if (resultLine.startsWith(user + ":")) {
				return;
			}
			resultLine = in.readLine();
		}
		throw new AssertionError("host " + remote.getHost() + " is not have user [" + user + "]");
	}

	public void assertFile(String path, String mode, String group, String owner, Boolean isDir) throws IOException, AssertionError {
		String lsValue = this.remote.execute("ls -aldpF --color=no --time-style=+'%Y/%m/%d %H:%M:%S' " + path);
		try {
			LsResult lsResult = ScriptingUtil.parseLs(lsValue);
			if (null != owner && !lsResult.owner.equals(owner)) {
				throw new AssertionError("owner expect[" + owner + "] but was [" + lsResult.owner + "]");
			}
			if (null != group && !lsResult.group.equals(group)) {
				throw new AssertionError("group expect[" + group + "] but was [" + lsResult.group + "]");
			}
			if (null != mode && !lsResult.mode.equals(mode)) {
				throw new AssertionError("mode expect[" + mode + "] but was [" + lsResult.mode + "]");
			}
			if (null != isDir && !lsResult.isDir == isDir) {
				throw new AssertionError("isDir expect[" + isDir + "] but was [" + lsResult.isDir + "]");
			}
		} catch (ParseException e) {
			throw new AssertionError(e);
		}
	}

	public void assertCommand(String command, String expect) throws IOException {
		String result = remote.execute(command);
		if (null == result) {
			throw new AssertionError("host " + remote.getHost() + "'s command [" + command + "] is null");
		}
		Pattern pattern = Pattern.compile(expect);
		Matcher matcher = pattern.matcher(result.trim());
		if (!matcher.matches()) {
			throw new AssertionError("host " + remote.getHost() + "'s command [" + command + "] expect [" + expect + "] but was [" + result + "]");
		}
	}

	public void assertFileHasLine(String filePath, String line) throws IOException {
		String result = remote.execute("cat " + filePath);
		BufferedReader in = new BufferedReader(new StringReader(result));
		String resultLine = in.readLine();
		while (null != resultLine) {
			if (line.trim().equals(resultLine.trim())) {
				return;
			}
			resultLine = in.readLine();
		}
		throw new AssertionError("host " + remote.getHost() + "'s file [" + filePath + "] is not have line [" + line + "]");
	}

	public void assertPortOpen(int portNumber) throws UnknownHostException, IOException {
		try {
			Socket socket = new Socket(remote.getHost(), portNumber);
			socket.close();
		} catch (Exception e) {
			throw new AssertionError("host " + remote.getHost() + "'s port [" + portNumber + "] is not open", e);
		}
	}

	@SuppressWarnings("serial")
	public static class AssertionError extends Error {

		public AssertionError() {
			super();
		}

		public AssertionError(String message, Throwable cause) {
			super(message, cause);
		}

		public AssertionError(String message) {
			super(message);
		}

		public AssertionError(Throwable cause) {
			super(cause);
		}

	}

	public Remote getRemote() {
		return remote;
	}

	public void setRemote(Remote remote) {
		this.remote = remote;
	}
}
