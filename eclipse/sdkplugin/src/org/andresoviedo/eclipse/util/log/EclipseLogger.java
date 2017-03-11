package org.andresoviedo.eclipse.util.log;

import org.andresoviedo.eclipse.sdkplugin.Activator;
import org.eclipse.core.runtime.Status;

public final class EclipseLogger {

	private EclipseLogger() {
	}

	public static void info(String msg) {
		info(msg, null);
	}

	public static void info(String msg, Exception e) {
		Activator
				.getDefault()
				.getLog()
				.log(new Status(Status.INFO, Activator.PLUGIN_ID, Status.OK,
						msg, e));
	}

	public static void error(String msg) {
		error(msg, null);
	}

	public static void error(String msg, Exception e) {
		Activator
				.getDefault()
				.getLog()
				.log(new Status(Status.ERROR, Activator.PLUGIN_ID,
						Status.ERROR, msg, e));
	}

	public static void warn(String string) {
		warn(string, null);
	}

	public static void warn(String msg, Exception ex) {
		Activator
				.getDefault()
				.getLog()
				.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						Status.ERROR, msg, ex));

	}

}
