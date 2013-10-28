package org.andresoviedo.apps.installer.model;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class ModelTest {

	@BeforeClass
	public static void before() {
		Environment.putEnvEntry("user.dir", "src/test/resources/installer");
	}

	@Test
	public void testInstallChangeLog() throws Exception {
		File changeLog = Model.installChangeLog();
		FileUtils.forceDelete(changeLog);
	}
}
