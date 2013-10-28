package org.andresoviedo.apps.installer.model;

import java.io.File;

public final class Constants {

	public static final File INSTALLER_DIR = new File(Environment.getenv().get(
			"user.dir"));
	public static final File SETUP_DIRECTORY = new File(INSTALLER_DIR, "Setup");
	public static final File SETUP_CHANGELOG_FILE = new File(SETUP_DIRECTORY,
			"ChangeLog.html");

	public static final String IBM_IM_1_6_3_1 = "com.ibm.cic.agent_1.6.3001.20130528_1750";
	public static final String IBM_WAS_8_5_0 = "com.ibm.websphere.DEVELOPERSILAN.v85_8.5.0.20120501_1108";

}
