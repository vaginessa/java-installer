package org.andresoviedo.apps.installer.view.components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

/**
 * ProgressListener listens to "progress" property changes in the SwingWorkers
 * that search and load images.
 */
public class ProgressBar implements PropertyChangeListener {

	ProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
		this.progressBar.setValue(0);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String strPropertyName = evt.getPropertyName();
		if ("progress".equals(strPropertyName)) {
			progressBar.setIndeterminate(false);
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	private JProgressBar progressBar;
}
