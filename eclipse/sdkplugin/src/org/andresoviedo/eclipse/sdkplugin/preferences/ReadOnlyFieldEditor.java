package org.andresoviedo.eclipse.sdkplugin.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ReadOnlyFieldEditor extends StringFieldEditor {

	public ReadOnlyFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected void createControl(Composite parent) {
		super.createControl(parent);
		Text control = getTextControl();
		control.setEditable(false);
	}

	protected void setEnabled(boolean enabled) {
		Text control = getTextControl();
		control.setEnabled(enabled);
	}

}
