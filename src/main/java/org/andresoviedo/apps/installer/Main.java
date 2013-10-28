package org.andresoviedo.apps.installer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.andresoviedo.apps.installer.controller.Controller;
import org.andresoviedo.apps.installer.model.FileSystem;
import org.andresoviedo.apps.installer.model.Model;
import org.andresoviedo.apps.installer.services.FileSystemMonitorService;
import org.andresoviedo.apps.installer.view.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Application launcher.
 * 
 * @author generali
 * 
 */
public class Main {

	// MVC Components
	private static Model model;
	private static FileSystem fileSystemModel;
	private static Controller controller;
	private static View view;

	// Configuration
	public static Map<?, ?> properties;

	// Services Components
	private static FileSystemMonitorService fileSystemMonitorService;
	private static final Log logger = LogFactory.getLog(Main.class);

	public static void main(String[] args) {
		try {
			// Init services
			logger.info("Starting application...");

			logger.info("Loading application configuration...");
			File configurationFile = new File("install.properties");
			Properties properties = new Properties();
			if (configurationFile.exists()) {
				FileInputStream inStream = new FileInputStream(
						configurationFile);
				properties.load(inStream);
				inStream.close();
			}
			Main.properties = Collections.unmodifiableMap(properties);

			// Init MVC Components
			logger.info("Initializing application model...");
			model = new Model();
			fileSystemModel = new FileSystem();
			// fileSystemMonitorService = new FileSystemMonitorService(
			// fileSystemModel);

			logger.info("Initializing application Controller...");
			controller = new Controller(model);

			// Init View
			logger.info("Initializing application GUI...");
			view = new View(controller, model);
			view.setSize(640, 480);
			model.addObserver(view);
			fileSystemModel.addObserver(view);
			view.setLocationRelativeTo(null);
			view.setVisible(true);
			view.pack();

			if (args != null && args.length > 0) {
				if (args[0].equals("install")) {
					view.installDelayed();
				} else if (args[0].equals("uninstall")) {
					view.uninstallDelayed();
				}
			}

			logger.info("Application started.");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static Model getModel() {
		return model;
	}

	public static void exit() {
		logger.info("Exiting application...");
		// fileSystemMonitorService.close();
		view.dispose();
		logger.info("Good bye!");
		System.exit(0);
	}
}