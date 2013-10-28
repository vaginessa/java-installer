package org.andresoviedo.apps.installer.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Model extends Observable {

	private static final Log logger = LogFactory.getLog(Model.class);

	private final StringBuffer loggerMessages = new StringBuffer();

	private final List<Object> runningProcesses = new ArrayList<Object>();

	public Model() {
	}

	public void processLoggingEvent(String msg) {
		synchronized (loggerMessages) {
			loggerMessages.append(msg);
			if (loggerMessages.length() > 2000) {
				int cutIndex = loggerMessages.length() - 2000;
				int finalCutIndex = cutIndex;
				int newLineCutIndex = loggerMessages.indexOf("\n", cutIndex) + 1;
				if (newLineCutIndex >= cutIndex
						&& newLineCutIndex < (cutIndex + 200)) {
					finalCutIndex = newLineCutIndex;
				}
				loggerMessages.replace(0, finalCutIndex, "");
			}
		}
		setChanged();
		notifyObservers("logging");
	}

	public StringBuffer getLoggerMessages() {
		return loggerMessages;
	}

	public void installProduct() {
		logger.info("Installing product...");
		logger.info("Product installed");
	}

	public static File installChangeLog() throws IOException {
		logger.info("Installing ChangeLog...");
		File changeLog = new File(System.getProperty("user.dir"),
				"ChangeLog.html");
		FileUtils.copyFile(Constants.SETUP_CHANGELOG_FILE, changeLog);
		logger.info("ChangeLog installed");
		return changeLog;
	}

	public void uninstallProduct() {
		logger.info("Uninstalling product...");
		logger.info("Product uninstalled");
	}

	// -----------------------------

}