package org.andresoviedo.apps.installer.controller;

import java.util.concurrent.Future;

import org.andresoviedo.apps.installer.model.Model;
import org.andresoviedo.apps.installer.tasks.Task1;
import org.andresoviedo.apps.installer.tasks.Task2;
import org.andresoviedo.util.tasks.DependantTasksExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Controller {

	private static final Log logger = LogFactory.getLog(Controller.class);

	private final Model model;

	public Controller(Model model) {
		this.model = model;
	}

	public int installAll() throws Exception {
		logger.info("Installing product...");
		try {
			DependantTasksExecutor executor = new DependantTasksExecutor(10);

			Future<Integer> task1 = executor.submit(new Task1(), "1", null);
			Future<Integer> task2 = executor.submit(new Task2(), "2", new String[] { "1" });

			executor.shutdown();

			int status = task1.get() + task2.get();
			logger.info("ARQ-SDK installed. Status: " + status);
			return status;

		} catch (Exception ex) {
			logger.fatal(ex.getMessage(), ex);
			throw ex;
		}
	}

	public boolean isAnythingInstalled() {
		// return model.isEclipseInstalled()
		// || model.isEclipseWorkspaceInstalled()
		// || model.isWasConfigInstalled();
		// TODO: de momento asumismo que hay algo instalado
		return true;
	}

	public int uninstallAll() throws Exception {
		logger.info("Uninstalling ARQ-SDK...");

		model.uninstallProduct();

		logger.info("ARQ-SDK uninstalled");

		return 0;
	}
}