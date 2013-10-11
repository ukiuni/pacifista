package org.ukiuni.pacifista.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.ukiuni.pacifista.Local;
import org.ukiuni.pacifista.Runtime;

public class TestZipUtil {

	@Test
	public void testUnzip() throws IOException {
		File targetDirectory = new File("testData/unzipdir/");
		targetDirectory.mkdirs();
		try {
			ZipUtil.unzip(new File("testData/application.zip"), targetDirectory);
			assertTrue(new File(targetDirectory, "html/controll/index.html").isFile());
			assertTrue(new File(targetDirectory, "html/controll/resteartAccepted.html").isFile());
			assertTrue(new File(targetDirectory, "html/controll/stopAccepted.html").isFile());
			assertTrue(new File(targetDirectory, "html/front/index.html").isFile());
		} finally {
			new Local(new File("."), new Runtime(new File("."), new File("templates"), new File("plugins"), new HashMap<String, Object>())).remove(targetDirectory);
		}
	}
}
