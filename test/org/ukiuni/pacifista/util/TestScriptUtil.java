package org.ukiuni.pacifista.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ukiuni.pacifista.util.ScriptingUtil.LsResult;

public class TestScriptUtil {
	@Test
	public void testPicupParameter() {
		String url = "script.js?test1=param1&test2=param2";
		Map<String, String> paramMap = ScriptingUtil.pickupParameters(url);
		Assert.assertEquals(2, paramMap.size());
		Assert.assertEquals("param1", paramMap.get("test1"));
		Assert.assertEquals("param2", paramMap.get("test2"));
	}

	@Test
	public void testParseLs() throws ParseException {
		String lsValue = "-rw-r--r--. 1 root root 158 2010/01/12 22:28:12 /etc/hosts";
		LsResult lsResult = ScriptingUtil.parseLs(lsValue);
		Assert.assertEquals(false, lsResult.isDir);
		Assert.assertEquals("rw-r--r--", lsResult.mode);
		Assert.assertEquals("root", lsResult.group);
		Assert.assertEquals("root", lsResult.owner);
		Assert.assertEquals(158, lsResult.size);
		Assert.assertEquals(2010 - 1900, lsResult.date.getYear());
		Assert.assertEquals(0, lsResult.date.getMonth());
		Assert.assertEquals(12, lsResult.date.getDate());
		Assert.assertEquals(22, lsResult.date.getHours());
		Assert.assertEquals(28, lsResult.date.getMinutes());
		Assert.assertEquals(12, lsResult.date.getSeconds());
		Assert.assertEquals("/etc/hosts", lsResult.name);
	}

	@Test
	public void testParseLsDir() throws ParseException {
		String lsValue = "drwxr-xr-x. 64 root root 4096 2013/09/01 22:22:06 /etc/";
		LsResult lsResult = ScriptingUtil.parseLs(lsValue);
		Assert.assertEquals(true, lsResult.isDir);
		Assert.assertEquals("rw-r--r--", lsResult.mode);
		Assert.assertEquals("root", lsResult.group);
		Assert.assertEquals("root", lsResult.owner);
		Assert.assertEquals(4096, lsResult.size);
		Assert.assertEquals(2013 - 1900, lsResult.date.getYear());
		Assert.assertEquals(8, lsResult.date.getMonth());
		Assert.assertEquals(1, lsResult.date.getDate());
		Assert.assertEquals(22, lsResult.date.getHours());
		Assert.assertEquals(22, lsResult.date.getMinutes());
		Assert.assertEquals(06, lsResult.date.getSeconds());
		Assert.assertEquals("/etc/", lsResult.name);
	}
}
