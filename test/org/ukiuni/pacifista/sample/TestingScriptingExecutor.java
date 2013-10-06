package org.ukiuni.pacifista.sample;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

import org.ukiuni.pacifista.RemoteFactory;
import org.ukiuni.pacifista.util.ScriptingUtil;

public class TestingScriptingExecutor {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		try {
			ScriptingUtil.execFolder(new File("."), new File("./testingScripts"), new File("template"), new File("plugins"), null);
		} finally {
			RemoteFactory.closeAll();
		}
		if (false) {
			System.out.println("main fisnished");
			ThreadInfo[] infos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
			for (ThreadInfo threadInfo : infos) {
				System.out.println("Thread " + threadInfo.getThreadName());
				StackTraceElement[] stackTraceElement = threadInfo.getStackTrace();
				for (StackTraceElement stackTraceElement2 : stackTraceElement) {
					System.out.println("\t" + stackTraceElement2.getClassName() + ":" + stackTraceElement2.getLineNumber());
				}
			}
			System.out.println("main dumpend");
		}
	}
}
