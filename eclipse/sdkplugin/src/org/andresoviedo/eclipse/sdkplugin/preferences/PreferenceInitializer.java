package org.andresoviedo.eclipse.sdkplugin.preferences;

import java.io.File;

import org.andresoviedo.eclipse.sdkplugin.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferencePage.SDK_UPDATE_CHECKER_ENABLED, true);
		store.setDefault(PreferencePage.SDK_METADATA_FILE_REMOTE, "//remote-server/SDK/Setup/ChangeLog.html");
		store.setDefault(PreferencePage.SDK_METADATA_FILE_LOCAL,
				new File("/" + File.separator + "SDK" + File.separator + "%USER%" + File.separator + "ChangeLog.html")
						.getAbsolutePath());
		store.setDefault(PreferencePage.SDK_NOTIFICATION_HOUR, "08:30");
		store.setDefault(PreferencePage.SDK_LOG_REMOTE, "//remote-server/SDK/Logs/eclipse-sdkplugin.log");
	}

}
