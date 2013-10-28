package org.andresoviedo.apps.installer.model;

import java.util.HashMap;
import java.util.Map;

public class Environment {

	private static Map<String, String> customEnv = new HashMap<String, String>(
			System.getenv());

	public static void putEnvEntry(String key, String value) {
		customEnv.put(key, value);
	}

	public static Map<String, String> getenv() {
		return customEnv;
	}

	public static void reset() {
		customEnv = new HashMap<String, String>(System.getenv());
	}
}
