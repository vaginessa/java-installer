package org.andresoviedo.apps.installer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.andresoviedo.apps.installer.controller.Controller;
import org.andresoviedo.apps.installer.model.Model;
import org.andresoviedo.apps.installer.view.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Application launcher.
 * 
 */
public class Main {

	// MVC Components
	private static Model model;
	private static Controller controller;
	private static View view;

	// Configuration
	public static Map<?, ?> properties;

	// Services Components
	private static final Log logger = LogFactory.getLog(Main.class);

	public static void main(String[] args) {
		try {
			// Init services
			logger.info("Starting application...");

			logger.info("Loading application configuration...");
			File configurationFile = new File("Setup", "install.ini");
			Properties properties = new Properties();
			if (configurationFile.exists()) {
				FileInputStream inStream = new FileInputStream(
						configurationFile);
				properties.load(inStream);
				inStream.close();
			}
			Main.properties = Collections.unmodifiableMap(properties);

			if (Boolean.parseBoolean((String) properties
					.get("installer.disabled"))) {
				if (!Arrays.asList(
						((String) properties.get("installer.testers"))
								.split(",")).contains(
						System.getProperty("user.name").toLowerCase())) {
					JOptionPane
							.showMessageDialog(
									null,
									"Realizando tareas de mantenimiento.\nPor favor inténtelo de nuevo en unos minutos.");
					System.exit(0);
				}
			}

			// Init MVC Components
			logger.info("Initializing application model...");
			model = new Model();

			logger.info("Initializing application Controller...");
			controller = new Controller(model);

			// Init View
			logger.info("Initializing application GUI...");
			view = new View(controller, model);
			view.setSize(640, 480);
			model.addObserver(view);
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