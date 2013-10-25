package es.generali.arq_sdk.model;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.generali.arq_sdk.Main;
import es.generali.arq_sdk.util.io.IOHelper;
import es.generali.arq_sdk.util.run.RunHelper;
import es.generali.arq_sdk.util.zip.ZipHelper;

public class Model extends Observable {

	private static final Log logger = LogFactory.getLog(Model.class);

	private final StringBuffer loggerMessages = new StringBuffer();

	private final List<Object> runningProcesses = new ArrayList<Object>();

	// Utilities
	public static final File SETUP_DIRECTORY = new File("Setup");
	public static final File ROBOCOPY_PATH = new File(SETUP_DIRECTORY,
			"robocopy.exe");

	// Source directories
	public static final File SOURCE_DIR = new File(".");
	private static final File SOURCE_ORACLE_JDK_ZIP = new File("Programs"
			+ File.separator + "jdk1.6.0_45.zip");
	private static final File SOURCE_IBM_INSTALLATION_MANAGER_ZIP = new File(
			"Programs" + File.separator + "IBM_Install_Manager.zip");
	private static final File SOURCE_WAS_850_ZIP = new File("Programs"
			+ File.separator + "WAS850" + File.separator + "WAS.zip");
	private static final File SOURCE_WAS_852_ZIP = new File("Programs"
			+ File.separator + "WAS852" + File.separator + "WAS.zip");
	public static final File SOURCE_WAS_LIBS = new File("Libraries"
			+ File.separator + "Generali");
	public static final File SOURCE_WAS_INSTALLATION_RECORD = new File(
			SETUP_DIRECTORY, "was_installation_record.xml");
	public static final File SOURCE_WAS_UPDATE_RECORD = new File(
			SETUP_DIRECTORY, "was_update_record.xml");
	public static final File SOURCE_WAS850_UNINSTALLATION_RECORD = new File(
			SETUP_DIRECTORY, "was850_uninstallation_record.xml");
	public static final File SOURCE_WAS852_UNINSTALLATION_RECORD = new File(
			SETUP_DIRECTORY, "was852_uninstallation_record.xml");
	public static final File SOURCE_WAS_CONFIG = new File(SETUP_DIRECTORY,
			"was_configuration.py");
	public static final File SOURCE_WAS_CONFIG_DEMO = new File(SETUP_DIRECTORY,
			"was_configuration_demo.py");
	public static final File SOURCE_CREATE_START_MENU_FUNCTION = new File(
			SETUP_DIRECTORY, "createStartMenuItem.vbs");
	public static final File SOURCE_REMOVE_START_MENU_FUNCTION = new File(
			SETUP_DIRECTORY, "removeStartMenuItem.vbs");
	public static final File SOURCE_WAS_SCRIPT_GET_INFO = new File(
			SETUP_DIRECTORY, "was_info.py");
	public static final File SOURCE_NEWARQ_CONFIG = new File("Config"
			+ File.separator + "WAS");
	public static final File SOURCE_ECLIPSE_ZIP = new File("Programs",
			"eclipse" + File.separator + "eclipse.zip");
	public static final File SOURCE_ECLIPSE_CONFIG_DIR = new File("Config",
			"eclipse");
	public static final File SOURCE_ECLIPSE_INI_FILE = new File("Config",
			"eclipse" + File.separator + "eclipse.ini");
	public static final File SOURCE_ECLIPSE_WORKSPACE_DIR = new File("Config",
			"workspace");
	public static final File SOURCE_MAVEN_SETTINGS_FILE = new File("Config",
			"m2" + File.separator + "settings.xml");

	// Directorios según variables de entorno
	public static final String USERNAME = System.getProperty("user.name");
	public static final File USERHOME = new File(
			System.getProperty("user.home"));

	// Directorios de instalación
	public static final File ARQHOME = new File("C:\\ARQ-SDK", USERNAME);
	public static final File ECLIPSE_INSTALL_DIR = new File(ARQHOME, "eclipse");
	public static final File TARGET_ECLIPSE_WORKSPACE_DIR = new File(ARQHOME,
			"eclipse_workspace");
	public static final File WAS_CONFIG_DIR = new File("c:" + File.separator
			+ "usr" + File.separator + "dataappl");
	public static final File WAS_HOME = new File(ARQHOME, "IBM"
			+ File.separator + "WebSphere" + File.separator + "AppServer");
	public static final File IBM_IM_DIR = new File(ARQHOME, "IBM"
			+ File.separator + "Installation Manager");
	public static final String USERMENU = "Programas" + File.separator
			+ "ARQ-SDK";

	// Directorios importantes de los programas instalados
	public static final File WAS_LIBS_DIR = new File(WAS_HOME, "lib"
			+ File.separator + "generali");

	public static final File TARGET_ECLIPSE_DIR = new File(ARQHOME, "eclipse");
	public static final File TARGET_ECLIPSE_INI_FILE = new File(ARQHOME,
			"eclipse" + File.separator + "eclipse.ini");
	public static final File TARGET_MAVEN_SETTINGS_FILE = new File(ARQHOME,
			".m2" + File.separator + "settings.xml");
	public static final File ECLIPSE_WORKSPACE_MET_SETTINGS_FILE = new File(
			ARQHOME, "eclipse_workspace" + File.separator + ".metadata"
					+ File.separator + ".plugins" + File.separator
					+ "org.eclipse.core.runtime" + File.separator + ".settings"
					+ File.separator + "met-eclipse-plugin.prefs");
	public static final File TARGET_ECLIPSE_MYLYN_DIR = new File(
			TARGET_ECLIPSE_WORKSPACE_DIR, ".metadata" + File.separator
					+ ".mylyn");

	// Target programs
	public static final File WS_ADMIN = new File(WAS_HOME, "bin"
			+ File.separator + "wsadmin.bat");
	public static final File WS_PROFILE_MANAGER = new File(WAS_HOME, "bin"
			+ File.separator + "manageprofiles.bat");
	public static final File WS_START_SERVER = new File(WAS_HOME, "bin"
			+ File.separator + "startServer.bat");
	public static final File WS_STOP_SERVER = new File(WAS_HOME, "bin"
			+ File.separator + "stopServer.bat");
	public static final File IBM_INSTALLER = new File(IBM_IM_DIR, "eclipse"
			+ File.separator + "tools" + File.separator + "imcl.exe");
	public static final File IBM_UNINSTALLER = new File(IBM_IM_DIR, "uninstall"
			+ File.separator + "userinstc.exe");

	public static final File HOST_ORACLE_JAVA_HOME = new File(ARQHOME,
			"jdk1.6.0_45");
	public static final File HOST_ORACLE_JAVA_EXE = new File(
			HOST_ORACLE_JAVA_HOME, "bin" + File.separator + "javaw.exe");
	public static final File HOST_JAVA_HOME_IBM = new File(WAS_HOME, "java");
	public static final File HOST_ECLIPSE_EXE = new File(ARQHOME, "eclipse"
			+ File.separator + "eclipse.exe");
	public static final File HOST_WINDOWS_EXPLORER_EXE = new File("C:",
			"Windows" + File.separator + "explorer.exe");
	public static final File HOST_INTERNET_BROWSER_EXE = new File("C:",
			"Archivos de programa" + File.separator + "Internet Explorer"
					+ File.separator + "iexplore.exe");
	public static final File HOST_CHANGELOG_FILE = new File(ARQHOME,
			"ChangeLog.html");

	// Settings constants
	public static final String MET_PLUGIN_JIRA_USER = "jiraUsr";
	public static final String MET_PLUGIN_SVN_USER = "svnUsr";
	public static URL MET_PROGRAMMERS_MANUAL;
	static {
		try {
			MET_PROGRAMMERS_MANUAL = new URL(
					"https://ssl.generali.es/arq_wiki/display/NuevaArquitecturaGenerali/6.3.+Programador");
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	public Model() {
	}

	// public int getCurrentInstalledVersion() {
	// File versionFile = new File(ARQHOME, "version.txt");
	// if (versionFile.exists()) {
	// Properties p = new Properties();
	// p.load(new FileInputStream(versionFile));
	// return Integer.parseInt(p.getProperty("version"));
	// }
	// }

	public boolean areProcessesRunning() {
		return runningProcesses.size() > 0;
	}

	public static List<String> getIBMInstalledPackages() throws Exception {
		logger.info("Getting list of IBM installed packages...");
		if (!IBM_INSTALLER.exists()) {
			logger.info("No packages installed. IBM Installation Manager not installed");
			return new ArrayList<String>();
		}
		StringBuilder cmdOutput = new StringBuilder();
		RunHelper.exec(IBM_INSTALLER.getAbsolutePath(), cmdOutput,
				"listInstalledPackages");
		logger.info("List of IBM installed packages: " + cmdOutput);
		return Arrays.asList(cmdOutput.toString().split(
				System.getProperty("line.separator")));
	}

	public void killProcesses() {
		logger.info("Kllling processes...");
		synchronized (runningProcesses) {
			for (Object process : runningProcesses) {
				if (process instanceof Process) {
					((Process) process).destroy();
				}
			}
		}
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

	public void uninstallEclipse() throws IOException {
		logger.debug("Uninstalling eclipse '" + ECLIPSE_INSTALL_DIR + "'...");
		if (ECLIPSE_INSTALL_DIR.exists()) {
			FileUtils.forceDelete(ECLIPSE_INSTALL_DIR);
			logger.debug("Eclipse '" + ECLIPSE_INSTALL_DIR + "' uninstalled");
		}
	}

	@SuppressWarnings("unchecked")
	public void installEclipseWorkspace() throws Exception {
		logger.info("Instalando workspace...");

		// Initialize default jiraUser
		String jiraUser = USERNAME;
		String svnUser = USERNAME;

		// Try to preserve met-plugin configuration...
		if (ECLIPSE_WORKSPACE_MET_SETTINGS_FILE.exists()) {
			logger.debug("Reading user configuration...");
			Properties metSettings = new Properties();
			metSettings.load(new StringReader(FileUtils
					.readFileToString(ECLIPSE_WORKSPACE_MET_SETTINGS_FILE)));
			jiraUser = metSettings.getProperty(MET_PLUGIN_JIRA_USER);
			svnUser = metSettings.getProperty(MET_PLUGIN_SVN_USER);
			logger.debug("	* jiraUsr=" + jiraUser);
			logger.debug("	* svnUsr=" + svnUser);
		} else {
			logger.debug("User configuration not found");
		}

		// Dictionary to filter workspace files...
		@SuppressWarnings("rawtypes")
		Dictionary dictionary = new Hashtable<String, String>();
		dictionary.put("%USERNAME%", USERNAME);
		dictionary.put("%JIRA_USR%", jiraUser);
		dictionary.put("%SVN_USR%", svnUser);

		if (TARGET_ECLIPSE_MYLYN_DIR.exists()) {
			logger.debug("Limpiando configuración mylyn...");
			FileUtils.forceDelete(TARGET_ECLIPSE_MYLYN_DIR);
		}

		logger.debug("Copiando archivos de workspace...");
		IOHelper.copyAndFilter(SOURCE_ECLIPSE_WORKSPACE_DIR,
				TARGET_ECLIPSE_WORKSPACE_DIR, dictionary);

		logger.info("Workspace instalado.");
	}

	public void uninstallEclipseWorkspace() throws IOException {
		logger.debug("Uninstalling eclipse workspace '"
				+ TARGET_ECLIPSE_WORKSPACE_DIR + "'...");
		if (TARGET_ECLIPSE_WORKSPACE_DIR.exists()) {
			FileUtils.forceDelete(TARGET_ECLIPSE_WORKSPACE_DIR);
			logger.debug("Eclipse workspace '" + TARGET_ECLIPSE_WORKSPACE_DIR
					+ "' uninstalled");
		}
	}

	public boolean isEclipseWorkspaceInstalled() {
		return TARGET_ECLIPSE_WORKSPACE_DIR.exists();
	}

	public boolean isEclipseInstalled() {
		return Model.ECLIPSE_INSTALL_DIR.exists();
	}

	public boolean isWasConfigInstalled() {
		return WAS_CONFIG_DIR.exists();
	}

	public void uninstallIBMInstallManager() throws Exception {

		logger.info("Uninstalling IBM Install Manager...");

		if (!IBM_UNINSTALLER.exists()) {
			logger.info("El IBM Install Manager ya esta desinstalado");
			return;
		}

		RunHelper.exec(IBM_UNINSTALLER.getAbsolutePath(), null, null);

		// Asegurar el borrado
		if (IBM_IM_DIR.exists()) {
			FileUtils.forceDelete(IBM_IM_DIR);
		}

		logger.info("IBM Install Manager uninstalled");

	}

	public void uninstallWas() throws Exception {
		// TODO: Desinstalar según versión 850 o 852

		logger.info("Desinstalando Websphere Application Server...");
		if (!WAS_HOME.exists()) {
			logger.info("Websphere Application Server ya esta desinstalado");
			return;
		}

		File tempDir = null;
		File responsesFile1 = null;
		try {
			responsesFile1 = IOHelper.filterFile(
					SOURCE_WAS850_UNINSTALLATION_RECORD, "username", USERNAME);

			logger.debug("Generado fichero de respuestas en '" + responsesFile1
					+ "'");

			RunHelper.exec(IBM_INSTALLER.getAbsolutePath(), null,
					"-acceptLicense", "-accessRights", "nonAdmin", "-input",
					responsesFile1.getAbsolutePath(), "-dataLocation",
					IBM_IM_DIR.getAbsolutePath(), "-showProgress");

			logger.info("Websphere Application Server uninstalled");
		} finally {
			logger.debug("Eliminando ficheros temporales...");
			if (tempDir != null) {
				FileUtils.forceDelete(tempDir);
			}
			if (responsesFile1 != null) {
				responsesFile1.delete();
			}
			if (WAS_HOME.exists()) {
				logger.debug("Uninstalling WAS directory '" + WAS_HOME + "'...");
				FileUtils.forceDelete(WAS_HOME.getParentFile());
				logger.debug("WAS Config '" + WAS_HOME + "' uninstalled");
			}
		}
	}

	// public void updateWAS(File wasRepoDir) throws Exception {
	// installIBMPackage(SOURCE_WAS_UPDATE_RECORD);
	// }
	//
	// public void installIBMPackage(File responsesFile) throws Exception {
	// // TODO: Automatizar desinstalación del WAS
	//
	// logger.info("Desinstalando Websphere Application Server...");
	// if (!WAS_HOME.exists()) {
	// logger.info("Websphere Application Server ya esta desinstalado");
	// return;
	// }
	//
	// File tempDir = null;
	// File responsesFile1 = null;
	// try {
	// responsesFile1 = IOHelper.filterFile(responsesFile, "username",
	// USERNAME);
	//
	// logger.debug("Generado fichero de respuestas en '" + responsesFile1
	// + "'");
	//
	// RunHelper.exec(IBM_INSTALLER.getAbsolutePath(), null,
	// "-acceptLicense", "-accessRights", "nonAdmin", "-input",
	// responsesFile1.getAbsolutePath(), "-dataLocation",
	// IBM_IM_DIR.getAbsolutePath(), "-showProgress");
	//
	// logger.info("Websphere Application Server uninstalled");
	// } finally {
	// logger.debug("Eliminando ficheros temporales...");
	// if (tempDir != null) {
	// FileUtils.forceDelete(tempDir);
	// }
	// if (responsesFile1 != null) {
	// responsesFile1.delete();
	// }
	// if (WAS_HOME.exists()) {
	// logger.debug("Uninstalling WAS directory '" + WAS_HOME + "'...");
	// FileUtils.forceDelete(WAS_HOME.getParentFile());
	// logger.debug("WAS Config '" + WAS_HOME + "' uninstalled");
	// }
	// }
	// }

	public void uninstallWasConfig() throws IOException {
		logger.info("Uninstalling WAS logs...");

		// Delete arch logs (log4j configuration)

		File wasLogsDir = new File("c:", "var");
		if (wasLogsDir.exists()) {
			FileUtils.forceDelete(wasLogsDir);
		}

		// if (WAS_CONFIG_DIR.exists()) {
		// FileUtils.forceDelete(WAS_CONFIG_DIR.getParentFile());
		// logger.debug("WAS Config '" + WAS_CONFIG_DIR + "' uninstalled");
		// }

		logger.info("WAS logs uninstalled");
	}

	public void uninstallARQHome() throws IOException {
		logger.debug("Finishing uninstalling ...");
		if (ARQHOME.exists()) {
			FileUtils.forceDelete(ARQHOME);
		}
	}

	public File downloadIBMInstallationManager() throws Exception {
		File tempDir = null;
		logger.info("Descargando IBM Install Manager...");
		if (Boolean.valueOf((String) Main.properties.get("directCopy"))) {
			tempDir = ZipHelper.unzipFile(SOURCE_IBM_INSTALLATION_MANAGER_ZIP);
		} else {
			tempDir = ZipHelper
					.getFileThenUnzip(SOURCE_IBM_INSTALLATION_MANAGER_ZIP);
		}
		logger.info("IBM Install Manager descargado");
		return tempDir;
	}

	public boolean isIBMIMInstalled() {
		logger.info("Revisando instalación de IBM Install Manager...");
		if (new File(IBM_IM_DIR, "properties" + File.separator + "version"
				+ File.separator + "IBM_Installation_Manager.1.6.3.1.swtag")
				.exists()) {
			logger.info("IBM Installation Manager ya esta instalado.");
			return true;
		}

		if (IBM_IM_DIR.exists()) {
			logger.info("IBM Installation Manager ya esta instalado");
			return true;
		}

		return false;
	}

	public void installIBMInstallationManager(File installerDir)
			throws Exception {
		logger.info("Instalando IBM Install Manager...");
		int result = RunHelper.exec(
				new File(installerDir, "installc").getAbsolutePath(), null,
				"-acceptLicense", "-accessRights", "nonAdmin", "-dataLocation",
				IBM_IM_DIR.getAbsolutePath(), "-installationDirectory",
				new File(IBM_IM_DIR, "eclipse").getAbsolutePath(),
				"-showProgress");

		if (result != 0) {
			throw new RuntimeException(
					"Error en la instalación del IBM Installation Manager. Result="
							+ result);
		}
		logger.info("IBM Install Manager instalado");
	}

	// public void installWAS2() throws Exception {
	// File tempDir = null;
	// try {
	// logger.info("Validando instalación del WAS...");
	//
	// // Instalamos el WAS si aun no ha sido instalado
	// if (WAS_DIR.exists()) {
	// logger.info("El Directorio '"
	// + WAS_DIR
	// + "' ya existe. "
	// + "Si quiere reinstalar el WAS Por favor desinstale el WAS manualmente "
	// +
	// "y posteriormente elimine dicha carpeta. Después vuelva a ejecutar este instalador.");
	// return;
	// }
	//
	// tempDir = Helper.getFileThenUnzip(new File("." + File.separator
	// + "Programs" + File.separator + "IBM_Install_Manager.zip"));
	//
	// RunHelper.exec(new File(tempDir, "installc").getAbsolutePath(), null,
	// "-acceptLicense", "-accessRights", "nonAdmin");
	//
	// logger.info("IBM Install Manager instalado");
	// } finally {
	// if (tempDir != null) {
	// logger.debug("Eliminando directorio temporal '"
	// + tempDir.getAbsolutePath() + "'");
	// FileUtils.forceDelete(tempDir);
	// }
	// }
	// }

	public File getWAS850UncompressedRepo() throws Exception {
		return getInstallerUncompressedDirForZip(SOURCE_WAS_850_ZIP);
	}

	public File getWAS852UncompressedRepo() {
		return getInstallerUncompressedDirForZip(SOURCE_WAS_852_ZIP);
	}

	public File getIBMIMUncompressedRepo() {
		return getInstallerUncompressedDirForZip(SOURCE_IBM_INSTALLATION_MANAGER_ZIP);
	}

	public File getInstallerUncompressedDirForZip(File zipFile) {
		logger.info("Validando si existe repositorio local del fichero '"
				+ zipFile + "'...");

		if (!Boolean.valueOf((String) Main.properties.get("directCopy"))) {
			logger.debug("Repositorio descomprimido deshabilitado por configuración");
			return null;
		}

		String uncompressedRepoFolder = zipFile.getAbsolutePath().substring(0,
				zipFile.getAbsolutePath().lastIndexOf('.'));
		File localRepo = new File(uncompressedRepoFolder);
		return localRepo.exists() && localRepo.isDirectory() ? localRepo : null;
	}

	public File downloadWAS850() throws Exception {
		logger.info("Descargando el Websphere Application Server 850...");
		File tempDir = null;
		if (Boolean.valueOf((String) Main.properties.get("directCopy"))) {
			logger.debug("Descomprimiendo WAS...");
			tempDir = ZipHelper.unzipMultipartZip(SOURCE_WAS_850_ZIP);
		} else {
			logger.debug("Descargando y descomprimiendo WAS...");
			tempDir = ZipHelper.getMultipartZipThenUnzip(SOURCE_WAS_850_ZIP);
		}

		// Creamos el perfil WAS por defecto
		logger.info("Websphere Application Server 850 descargado");
		return tempDir;
	}

	// TODO: Modular con método anterior
	public File downloadWAS852() throws Exception {
		logger.info("Descargando el Websphere Application Server 852...");
		File tempDir = null;
		if (Boolean.valueOf((String) Main.properties.get("directCopy"))) {
			logger.debug("Descomprimiendo WAS...");
			tempDir = ZipHelper.unzipMultipartZip(SOURCE_WAS_852_ZIP);
		} else {
			logger.debug("Descargando y descomprimiendo WAS...");
			tempDir = ZipHelper.getMultipartZipThenUnzip(SOURCE_WAS_852_ZIP);
		}

		// Creamos el perfil WAS por defecto
		logger.info("Websphere Application Server 852 descargado");
		return tempDir;
	}

	public boolean isWASInstalled() {
		// Instalamos el WAS si aun no ha sido instalado
		if (WAS_HOME.exists()) {
			logger.debug("El Directorio '"
					+ WAS_HOME
					+ "' ya existe. "
					+ "Si quiere reinstalar el WAS Por favor desinstale el WAS manualmente "
					+ "y posteriormente elimine dicha carpeta. Después vuelva a ejecutar este instalador.");
			return true;
		}
		return false;
	}

	public void installWAS(File repoWas850, File repoWas852) throws Exception {
		File responsesFile = null;
		try {
			logger.info("Instalando el Websphere Application Server...");

			Dictionary<String, String> dictionary = new Hashtable<String, String>();
			dictionary.put("repo_was_850", repoWas850.getAbsolutePath());
			dictionary.put("repo_was_852", repoWas852.getAbsolutePath());
			dictionary.put("username", USERNAME);

			responsesFile = IOHelper.filterFile(SOURCE_WAS_INSTALLATION_RECORD,
					dictionary);

			logger.debug("Generado fichero de respuestas en '" + responsesFile
					+ "'");

			RunHelper.exec(IBM_INSTALLER.getAbsolutePath(), null,
					"-acceptLicense", "-accessRights", "nonAdmin", "-input",
					responsesFile.getAbsolutePath(), "-dataLocation",
					IBM_IM_DIR.getAbsolutePath(),
					// "-installationDirectory ", IBM_IM_DIR.getAbsolutePath(),
					"-showProgress");

			// Creamos el perfil WAS por defecto
			logger.debug("Creando perfil WAS por defecto...");
			RunHelper.execBat(WS_PROFILE_MANAGER.getAbsolutePath(), null,
					"-create");

			logger.info("Websphere Application Server installed");
		} finally {
			logger.debug("Eliminando ficheros temporales...");
			if (responsesFile != null) {
				responsesFile.delete();
			}
		}
	}

	public void startWas() throws Exception {
		logger.info("Starting WAS...");
		RunHelper.execBat(WS_START_SERVER.getAbsolutePath(), null, "server1");
		logger.info("WAS started");
	}

	public void stopWas() throws Exception {
		logger.info("Shutting down WAS...");
		RunHelper.execBat(WS_STOP_SERVER.getAbsolutePath(), null, "server1");
		logger.info("WAS shutted down");
	}

	public void configureWas() throws Exception {
		logger.info("Configurando servidor WAS...");

		logger.debug("Copiando librerías fenix...");
		RunHelper.exec(ROBOCOPY_PATH.getAbsolutePath(), null,
				SOURCE_WAS_LIBS.getAbsolutePath(),
				WAS_LIBS_DIR.getAbsolutePath());

		logger.debug("Cargando configuración de arquitectura...");
		RunHelper
				.execBat(
						WS_ADMIN.getAbsolutePath(),
						null,
						"-lang",
						"jython",
						"-f",
						SOURCE_WAS_CONFIG.getAbsolutePath(),
						SOURCE_DIR.getAbsolutePath().replace(
								File.separatorChar, '/'),
						WAS_HOME.getAbsolutePath().replace(File.separatorChar,
								'/'),
						WAS_CONFIG_DIR.getAbsolutePath().replace(
								File.separatorChar, '/'),
						WAS_LIBS_DIR.getAbsolutePath().replace(
								File.separatorChar, '/'));

		logger.debug("Cargando configuración de DEMO...");
		RunHelper.execBat(WS_ADMIN.getAbsolutePath(), null, "-lang", "jython",
				"-f", SOURCE_WAS_CONFIG_DEMO.getAbsolutePath());

		logger.info("Servidor WAS configurado");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void installWasConfig() throws Exception {
		logger.info("Instalando configuración del WAS...");

		logger.debug("Extrayendo properties del WAS...");

		StringBuilder cmdOutput = new StringBuilder();
		RunHelper.execBat(WS_ADMIN.getAbsolutePath(), cmdOutput, "-lang",
				"jython", "-f", SOURCE_WAS_SCRIPT_GET_INFO.getAbsolutePath());

		Properties wasInfo = new Properties();
		wasInfo.load(new StringReader(cmdOutput.toString()));

		logger.debug("Copiando configuración de arquitectura a '"
				+ WAS_CONFIG_DIR + "'...");
		WAS_CONFIG_DIR.mkdirs();

		try {
			IOHelper.copyAndFilter(SOURCE_NEWARQ_CONFIG, WAS_CONFIG_DIR,
					(Dictionary) wasInfo);
		} catch (Exception ex) {
			logger.error("Exception copying '" + SOURCE_NEWARQ_CONFIG
					+ "' to '" + WAS_CONFIG_DIR
					+ "'. Assuming access denied. Ignoring error...");
		}
		logger.info("Configuración del WAS instalada");

	}

	public void installJDK() throws IOException {
		logger.info("Installing JDK...");
		if (HOST_ORACLE_JAVA_HOME.exists()) {
			logger.info("JDK already installed");
			return;
		}
		ZipHelper.unzipFile(SOURCE_ORACLE_JDK_ZIP, ARQHOME);
	}

	public void installEclipse() throws IOException {
		logger.info("Installing eclipse...");
		if (TARGET_ECLIPSE_DIR.exists()) {
			logger.info("El Directorio '"
					+ TARGET_ECLIPSE_DIR
					+ "' ya existe. "
					+ "Si quiere reinstalar el eclipse por favor elimine dicha carpeta. "
					+ "Después vuelva a ejecutar la instalación.");
			return;
		}
		// NOTA: El zip de eclipse ya contiene una carpeta "eclipse"; por eso
		// descomprimimos en el directorio padre

		// Helper.getFileThenUnzip(SOURCE_ECLIPSE_ZIP,
		// TARGET_ECLIPSE_DIR.getParentFile());
		if (Boolean.valueOf((String) Main.properties.get("directCopy"))) {
			ZipHelper.unzipMultipartZip(SOURCE_ECLIPSE_ZIP, TARGET_ECLIPSE_DIR);
		} else {
			ZipHelper.getMultipartZipThenUnzip(SOURCE_ECLIPSE_ZIP,
					TARGET_ECLIPSE_DIR);
		}

		logger.info("eclipse installed");

		// Helper.unzipFile(SOURCE_ECLIPSE_ZIP,
		// TARGET_ECLIPSE_DIR.getParentFile());
	}

	public void configureEclipse() throws IOException {
		logger.info("Configurando eclipse...");

		logger.debug("Actualizando plugins de eclipse...");
		FileUtils.copyDirectory(SOURCE_ECLIPSE_CONFIG_DIR, TARGET_ECLIPSE_DIR);

		logger.debug("Actualizando 'eclipse.ini'...");
		IOHelper.copyAndFilter(SOURCE_ECLIPSE_INI_FILE,
				TARGET_ECLIPSE_INI_FILE, "jvm_exe",
				HOST_ORACLE_JAVA_EXE.getAbsolutePath());

		logger.debug("Copiando maven settings '" + SOURCE_MAVEN_SETTINGS_FILE
				+ "'==>'" + TARGET_MAVEN_SETTINGS_FILE + "'...");
		FileUtils.copyFile(SOURCE_MAVEN_SETTINGS_FILE,
				TARGET_MAVEN_SETTINGS_FILE);

		logger.debug("Eclipse configurado");
	}

	public void installStartMenuItems() throws Exception {
		logger.debug("Instalando accesos directos...");

		installStartMenuItem(HOST_ECLIPSE_EXE.getAbsolutePath(), "-data "
				+ TARGET_ECLIPSE_WORKSPACE_DIR, "eclipse");

		installStartMenuItem(HOST_WINDOWS_EXPLORER_EXE.getAbsolutePath(),
				WAS_CONFIG_DIR.getAbsolutePath(), "WAS_Config");

		installStartMenuItem(HOST_INTERNET_BROWSER_EXE.getAbsolutePath(),
				MET_PROGRAMMERS_MANUAL.toString(), "Manual Desarrollador");

		logger.debug("Accesos directos instalados");
	}

	public void uninstallStartMenuItems() throws Exception {
		logger.info("Uninstalling StartMenu items...");
		RunHelper.exec("cscript.exe", null,
				SOURCE_REMOVE_START_MENU_FUNCTION.getAbsolutePath(), USERMENU);
	}

	private void installStartMenuItem(String command, String args,
			String description) throws Exception {
		logger.info("Installing StartMenu item '" + description + "'...");
		RunHelper.exec("cscript.exe", null,
				SOURCE_CREATE_START_MENU_FUNCTION.getAbsolutePath(), USERMENU,
				command, args, description);
		logger.info("StartMenu item '" + description + "' installed");
	}

	public void installChangeLog() throws IOException {
		logger.info("Installing ChangeLog...");
		FileUtils.copyInputStreamToFile(
				Model.class.getResourceAsStream("/ChangeLog.html"),
				HOST_CHANGELOG_FILE);
		logger.info("ChangeLog installed");
	}

	// -----------------------------

}