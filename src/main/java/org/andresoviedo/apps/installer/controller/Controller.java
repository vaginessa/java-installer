package org.andresoviedo.apps.installer.controller;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.andresoviedo.apps.installer.model.Model;
import org.andresoviedo.util.desktop.DesktopHelper;
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
		ExecutorService executor = null;
		try {
			executor = Executors.newFixedThreadPool(2);
			FutureTask<Integer> ibmStep = new FutureTask<Integer>(
					new Callable<Integer>() {
						@Override
						public Integer call() {
							logger.info("Starting installation step 1...");
							logger.info("Installation step 1 ended");
							return 0;
						}
					});

			FutureTask<Integer> eclipseStep = new FutureTask<Integer>(
					new Callable<Integer>() {
						@Override
						public Integer call() {
							logger.info("Starting installation step 2...");
							logger.info("Installation step 2 ended");
							return 0;
						}
					});

			executor.execute(ibmStep);
			executor.execute(eclipseStep);

			while (!ibmStep.isDone() || !eclipseStep.isDone()) {
				Thread.sleep(1000);
			}

			File changeLog = model.installChangeLog();
			logger.debug("Opening ChangeLog...");
			DesktopHelper.openWebpage(changeLog.toURI());

			logger.info("ARQ-SDK installed");
			return ibmStep.get() + eclipseStep.get();

		} catch (Exception ex) {
			logger.fatal(ex.getMessage(), ex);
			throw ex;
		} finally {
			if (executor != null) {
				executor.shutdown();
			}
		}
	}

	public int uninstallAll() throws Exception {
		logger.info("Uninstalling ARQ-SDK...");
		
		model.uninstallProduct();
		
		logger.info("ARQ-SDK uninstalled");

		return 0;
	}
}