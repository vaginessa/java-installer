package org.andresoviedo.apps.installer.tasks;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Task1 implements Callable<Integer> {

	private static final Log logger = LogFactory.getLog(Task1.class);

	@Override
	public Integer call() throws Exception {
		logger.info("Starting installation step 1...");
		Thread.sleep((long) (Math.random() * 1000));
		logger.info("Installation step 1 ended");
		return 0;
	}

}
