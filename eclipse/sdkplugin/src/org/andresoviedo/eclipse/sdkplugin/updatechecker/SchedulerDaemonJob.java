package org.andresoviedo.eclipse.sdkplugin.updatechecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.andresoviedo.eclipse.sdkplugin.preferences.PreferencePage;
import org.andresoviedo.eclipse.util.log.EclipseLogger;
import org.andresoviedo.eclipse.util.view.ViewHelper;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class SchedulerDaemonJob extends Job {

	private Job updateCheckDailyJob;
	private long dateLastNotification = -1;

	public SchedulerDaemonJob() {
		super("SDK Update Checker");
		setSystem(true);
		setPriority(Job.SHORT);
		updateCheckDailyJob = new CheckJob();
	}

	@Override
	protected void canceling() {
		if (updateCheckDailyJob != null) {
			updateCheckDailyJob.cancel();
		}
		super.canceling();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		while (!monitor.isCanceled()) {
			// Sleep until next day and wake up at some specified time
			try {
				long now = System.currentTimeMillis();

				// Parse time from preferences
				Calendar ct = Calendar.getInstance();
				ct.setTimeInMillis(new SimpleDateFormat("HH:mm").parse(PreferencePage.getNotificationHour()).getTime());

				// Set time from preferences
				Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, ct.get(Calendar.HOUR_OF_DAY));
				c.set(Calendar.MINUTE, ct.get(Calendar.MINUTE));
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);

				// Si ya hemos notificado hoy, esperamos al d�a siguiente
				if (dateLastNotification == c.getTimeInMillis()) {
					c.add(Calendar.HOUR, 24);
				}

				// Esperamos hasta la siguiente notificaci�n
				long timeout = c.getTimeInMillis() - now;
				if (timeout > 0) {
					EclipseLogger.info("Next update check scheduled at '" + c.getTime() + "'. Sleeping...");
					super.schedule(timeout);
					// schedule(10000);
					return Status.OK_STATUS;
				}

				dateLastNotification = c.getTimeInMillis();
				updateCheckDailyJob.schedule();

			} catch (ParseException e) {
				throw new IllegalArgumentException("Error parsing time", e);
			} catch (Exception ex) {
				EclipseLogger.error("Unexpected exception", ex);
				ViewHelper.createErrorMessageBox("Error at sdkplugin." + " Please check error log.").open();
			}
		}
		return Status.OK_STATUS;
	}

	static boolean isNewUpdateAvailable() {
		int versionLocal = 0;
		try {
			versionLocal = 1;
		} catch (Exception ex) {
			EclipseLogger.warn("Exception getting local version. Assuming no version.", ex);
		}
		int versionRemote = -1;
		try {
			versionRemote = 1;
		} catch (Exception ex) {
			EclipseLogger.warn(
					"Exception getting remote version. Assuming update notifications are centrally disabled.", ex);
		}

		EclipseLogger.info("Local vs Remote version: " + versionLocal + " vs " + versionRemote);

		return versionRemote > versionLocal;
	}

	static boolean isEnabled() {
		return PreferencePage.isUpdateCheckerEnabled();
	}
}
