package org.ukiuni.pacifista.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.ukiuni.pacifista.util.FileUtil.DirectoryPartAndFileName;

public class TestFileUtil {
	@Test
	public void testSeparateDirectoryAndFileName() {
		String test = "/test/dank.txt";
		DirectoryPartAndFileName dpafn = FileUtil.dividePathToParentDirectoryAndFileName(test);
		Assert.assertEquals("/test", dpafn.getDirectoryPart());
		Assert.assertEquals("dank.txt", dpafn.getFileName());
	}

	@Test
	public void testSeparateDirectoryAndFileName2() {
		String test = "dank.txt";
		DirectoryPartAndFileName dpafn = FileUtil.dividePathToParentDirectoryAndFileName(test);
		Assert.assertNull(dpafn.getDirectoryPart());
		Assert.assertEquals("dank.txt", dpafn.getFileName());
	}

	@Test
	public void testpathToFileAbsolute() {
		String test = "/absolute/valval.txt";
		File resultFile = FileUtil.pathToFile(new File("."), test);
		Assert.assertEquals(test, resultFile.getAbsolutePath());
	}

	@Test
	public void testpathToFileCalonical() {
		String test = "valval.txt";
		File resultFile = FileUtil.pathToFile(new File("."), test);
		Assert.assertEquals(new File(".").getParentFile().getAbsolutePath(), resultFile.getParentFile().getAbsolutePath());
	}
}
