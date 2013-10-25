package es.generali.arq_sdk.controller;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.generali.arq_sdk.model.Constants;
import es.generali.arq_sdk.model.Model;
import es.generali.arq_sdk.util.desktop.DesktopHelper;

public class Controller {

	private static final Log logger = LogFactory.getLog(Controller.class);

	private final Model model;

	public Controller(Model model) {
		this.model = model;
	}

	public int installAll() throws Exception {
		logger.info("Installing ARQ-SDK...");
		ExecutorService executor = null;
		try {
			executor = Executors.newFixedThreadPool(2);

			FutureTask<Integer> ibmStep = new FutureTask<Integer>(
					new Callable<Integer>() {
						@Override
						public Integer call() {

							Executor executor2 = Executors
									.newSingleThreadExecutor();
							FutureTask<Integer> ibmIMInstallTask = null;

							boolean isIBMIMRepoASetupDir = true;
							File ibmIMIRepoDir = null;
							try {
								logger.debug("Starting ibmStep...");

								// Descargamos IBM IM
								if (!model.isIBMIMInstalled()) {

									ibmIMIRepoDir = model
											.getIBMIMUncompressedRepo();
									if (ibmIMIRepoDir == null) {
										isIBMIMRepoASetupDir = false;
										ibmIMIRepoDir = model
												.downloadIBMInstallationManager();
									}

									// Instalamos IBM IM de forma asíncrona
									ibmIMInstallTask = createInstallIBMInstallationManagerTask(ibmIMIRepoDir);
									executor2.execute(ibmIMInstallTask);

								}

								// De momento esto sólo es informativo
								if (model.isWASInstalled()) {
									// Update if necessary
									List<String> imbInstalledPackages = model
											.getIBMInstalledPackages();
									if (imbInstalledPackages
											.contains(Constants.IBM_WAS_8_5_0)) {
										logger.info("Updating IBM Websphere Application Server...");
									} else {
										logger.info("IBM Websphere Application Server is up to date!");
									}

								}

								boolean isWASRepoASetupDir = true;
								File repoWas850 = model
										.getWAS850UncompressedRepo();
								File repoWas852 = model
										.getWAS852UncompressedRepo();
								if (repoWas850 == null || repoWas852 == null) {
									isWASRepoASetupDir = false;
									repoWas850 = model.downloadWAS850();
									repoWas852 = model.downloadWAS850();
								}

								// Esperamos a que el IBM IM acabe de
								// instalarse...
								if (ibmIMInstallTask != null) {
									if (!ibmIMInstallTask.isDone()) {
										logger.debug("Waiting for IBM Install Manager finishes installation...");
									}
									if (ibmIMInstallTask.get() != 0) {
										throw new RuntimeException(
												"Unexpected error on IBM IM Installation Task...");
									}

									if (!isIBMIMRepoASetupDir) {
										logger.info("Deleting temporary files...");
										FileUtils.forceDelete(ibmIMIRepoDir);
										logger.info("Temporary files deleted");
									}
								}
								model.installWAS(repoWas850, repoWas852);
								if (!isWASRepoASetupDir) {
									logger.info("Deleting temporary files...");
									FileUtils.forceDelete(repoWas850);
									FileUtils.forceDelete(repoWas852);
									logger.info("Temporary files deleted");
								}

								model.startWas();
								model.configureWas();
								model.installWasConfig();
								model.stopWas();
								return 0;
							} catch (Exception ex) {
								logger.fatal(
										"Se ha producido una excepción en el ibmStep",
										ex);
								return -1;
							} finally {
								// TODO: Matar el proceso de instalación del IBM
								// IM
							}
						}
					});

			FutureTask<Integer> eclipseStep = new FutureTask<Integer>(
					new Callable<Integer>() {
						@Override
						public Integer call() {
							try {
								logger.debug("Starting eclipsStep...");
								model.installJDK();
								model.installEclipse();
								model.configureEclipse();
								model.installEclipseWorkspace();
								model.installStartMenuItems();
								return 0;
							} catch (Exception ex) {
								logger.fatal(
										"Se ha producido una excepción en el eclipseStep",
										ex);
								return -1;
							}
						}
					});

			executor.execute(ibmStep);
			executor.execute(eclipseStep);

			while (!ibmStep.isDone() || !eclipseStep.isDone()) {
				Thread.sleep(1000);
			}

			model.installChangeLog();
			logger.debug("Opening ChangeLog...");
			DesktopHelper.openWebpage(Model.HOST_CHANGELOG_FILE.toURI());

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

	public FutureTask<Integer> createInstallIBMInstallationManagerTask(
			final File installerDir) throws Exception {
		FutureTask<Integer> installTask = new FutureTask<Integer>(
				new Callable<Integer>() {
					@Override
					public Integer call() {
						try {
							model.installIBMInstallationManager(installerDir);
							return 0;
						} catch (Exception ex) {
							logger.fatal(
									"Exception installing IBM IM. Please review log",
									ex);
							return -1;
						}
					}
				});
		return installTask;
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
		model.uninstallStartMenuItems();
		model.uninstallEclipseWorkspace();
		model.uninstallEclipse();
		model.uninstallWasConfig();
		model.uninstallWas();
		model.uninstallIBMInstallManager();
		model.uninstallARQHome();
		logger.info("ARQ-SDK uninstalled");

		return 0;
	}
}