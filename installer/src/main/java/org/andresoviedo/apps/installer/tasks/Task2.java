package org.andresoviedo.apps.installer.tasks;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Task2 implements Callable<Integer> {

	private static final Log logger = LogFactory.getLog(Task2.class);

	@Override
	public Integer call() throws Exception {
		logger.info("Starting installation step 2...");
		Thread.sleep((long) (Math.random() * 1000));
		logger.info("Installation step 2 ended");
		return 0;
	}

}
