package org.andresoviedo.eclipse.util.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

public final class ViewHelper {

	private ViewHelper() {
	}

	public static MessageBox createErrorMessageBox(String msg) {
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				SWT.ICON_ERROR | SWT.OK);
		messageBox.setText("Error");
		messageBox.setMessage(msg);
		return messageBox;
	}

	public static MessageDialog createInfoMessageDialog(String msg, String... options) {
		return new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Info", null, msg,
				MessageDialog.INFORMATION, options, 1);
	}

	public static MessageBox crateDisableAlertsConfirmDialog() {
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Warn");
		messageBox.setMessage("Are you sure you want to disable update notifications?"
				+ "\nYou can always re-enable it in Window > Preferences > SDK");
		return messageBox;
	}

}
