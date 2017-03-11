package org.andresoviedo.eclipse.sdkplugin.updatechecker;

import java.util.Arrays;
import java.util.List;

import org.andresoviedo.eclipse.sdkplugin.Activator;
import org.andresoviedo.eclipse.util.log.EclipseLogger;
import org.andresoviedo.eclipse.util.view.ViewHelper;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

class CheckJob extends Job {

	CheckJob() {
		super("SDK plugin");
		setUser(true);
		setPriority(Job.SHORT);
		addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				super.done(event);

				// Check job result and alert when error
				IStatus status = event.getJob().getResult();
				if (status.getSeverity() == Status.ERROR) {
					String errorMsg = "Error checking for SDK updates.";
					EclipseLogger.error(errorMsg, (Exception) status.getException());
					ViewHelper.createErrorMessageBox(errorMsg + " Please check error log.").open();
				}
			}
		});
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Checking for updates...", 100);
			if (!SchedulerDaemonJob.isEnabled()) {
				EclipseLogger.info("Update notifications disabled by configuration. Sleeping...");
				monitor.worked(100);
				return Status.OK_STATUS;
			}

			if (!SchedulerDaemonJob.isNewUpdateAvailable()) {
				EclipseLogger.info("Local installation is up to date. Sleeping...");
				monitor.worked(100);
				return Status.OK_STATUS;
			}
			monitor.worked(50);

			if (monitor.isCanceled()) {
				EclipseLogger.info("Update check cancelled by user. Sleeping...");
				monitor.worked(100);
				return Status.CANCEL_STATUS;
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					List<String> changeList = Arrays.asList(new String[] { "change 1", "change 2" });
					StringBuffer changes = new StringBuffer();
					for (String change : changeList) {
						changes.append('\n').append(change);
					}

					MessageDialog d = ViewHelper.createInfoMessageDialog("Detected SDK updates:\n" + changes,
							new String[] { "OK. Thanks for the info!" });
					int result = d.open();

					switch (result) {
					case 0:
						if (true) {
							// TODO;
							break;
						}

						MessageBox messageBox = ViewHelper.crateDisableAlertsConfirmDialog();
						int result2 = messageBox.open();
						switch (result2) {
						case SWT.OK:
							// user disabled notifications
							try {
								// PreferencePage
								// .setUpdateCheckerEnabled(false);
								EclipseLogger.info("Notificaciones desactivadas por el usuario.");
							} catch (Exception ex) {
								String errorMsg = "Error. Check log please";
								EclipseLogger.error(errorMsg, ex);
								ViewHelper.createErrorMessageBox(errorMsg).open();
							}
							break;
						case SWT.CANCEL:
							break;
						default:
							EclipseLogger.error("unexpected response '" + result2 + "'");
							break;
						}
					case 1:
					case SWT.DEFAULT:
						break;
					default:
						EclipseLogger.error("unexpected response '" + result + "'");
					}
				}
			});
			return Status.OK_STATUS;
		} catch (Exception ex) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
		} finally {
			monitor.worked(100);
			monitor.done();
		}
	}
}