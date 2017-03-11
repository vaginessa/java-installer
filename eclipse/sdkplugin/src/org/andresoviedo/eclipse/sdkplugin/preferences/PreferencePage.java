package org.andresoviedo.eclipse.sdkplugin.preferences;

import org.andresoviedo.eclipse.sdkplugin.Activator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

public final class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	final static String SDK_UPDATE_CHECKER_ENABLED = "enabled";
	final static String SDK_METADATA_FILE_REMOTE = "metadata_file_remote";
	final static String SDK_METADATA_FILE_LOCAL = "metadata_file_local";
	final static String SDK_LOG_REMOTE = "log_remote";
	final static String SDK_NOTIFICATION_HOUR = "notification_hour";

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	public void createFieldEditors() {
		BooleanFieldEditor b = new BooleanFieldEditor(SDK_UPDATE_CHECKER_ENABLED, "Check for updates and notify me",
				getFieldEditorParent());
		b.setEnabled(false, getFieldEditorParent());
		addField(b);
		final ReadOnlyFieldEditor r = new ReadOnlyFieldEditor(SDK_METADATA_FILE_REMOTE, "ChangeLog file (remote)",
				getFieldEditorParent());
		addField(r);
		final ReadOnlyFieldEditor r2 = new ReadOnlyFieldEditor(SDK_METADATA_FILE_LOCAL, "ChangeLog file (local)",
				getFieldEditorParent());
		addField(r2);
		b.setPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				System.out.println("Changed property");
			}
		});
		final ReadOnlyFieldEditor r3 = new ReadOnlyFieldEditor(SDK_NOTIFICATION_HOUR, "Hora del día para notificar",
				getFieldEditorParent());
		addField(r3);
		final ReadOnlyFieldEditor r4 = new ReadOnlyFieldEditor(SDK_LOG_REMOTE, "Log file (remote)",
				getFieldEditorParent());
		addField(r4);
	}

	public void init(IWorkbench workbench) {
	}

	public static boolean isUpdateCheckerEnabled() {
		// saves plugin preferences at the workspace level
		return Activator.getDefault().getPreferenceStore().getBoolean(SDK_UPDATE_CHECKER_ENABLED);
	}

	public static void setUpdateCheckerEnabled(boolean value) {
		InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).putBoolean(SDK_UPDATE_CHECKER_ENABLED, value);
		try {
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String getMetadataFileRemote() {
		// saves plugin preferences at the workspace level
		return Activator.getDefault().getPreferenceStore().getString(SDK_METADATA_FILE_REMOTE);
	}

	public static String getMetadataFileLocal() {
		// saves plugin preferences at the workspace level
		return Activator.getDefault().getPreferenceStore().getString(SDK_METADATA_FILE_LOCAL);
	}

	public static String getNotificationHour() {
		// return InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).get(
		// SDK_NOTIFICATION_HOUR, "09:00");
		return Activator.getDefault().getPreferenceStore().getString(SDK_NOTIFICATION_HOUR);
	}

	public static String getLogRemote() {
		return Activator.getDefault().getPreferenceStore().getString(SDK_LOG_REMOTE);
	}
}