package es.generali.arq_sdk.view.components;

import java.awt.Component;
import java.awt.Dialog;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class TimeoutOptionPane extends JOptionPane {

	private JLabel messageLabel = new JLabel();
	private Timer t = new Timer(true);
	private Dialog dialog;

	public TimeoutOptionPane(final Component parentComponent,
			final String message, final String title, int messageType,
			int optionType, long delay, final int seconds,
			final CallBack callback) {
		super.setMessage(messageLabel);
		super.setMessageType(messageType);
		super.setOptionType(optionType);
		messageLabel.setText(MessageFormat.format(message,
				String.valueOf(seconds)));

		dialog = createDialog(parentComponent, title);
		dialog.pack();
		dialog.setLocationRelativeTo(parentComponent);

		TimerTask showDialogTask = new TimerTask() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						dialog.setVisible(true);
						t.cancel();
						dialog.dispose();
						if (getValue() == null
								|| getValue() == UNINITIALIZED_VALUE) {
							callback.process(JOptionPane.CLOSED_OPTION);
						} else
							callback.process((Integer) getValue());
					}
				});
			}
		};

		TimerTask updateDialogMessageTask = new TimerTask() {
			int secondsToFinish = seconds;

			@Override
			public void run() {
				try {
					secondsToFinish--;
					final String messageWithSeconds = MessageFormat.format(
							(String) message, secondsToFinish);
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							messageLabel.setText((messageWithSeconds));
						}
					});

					if (secondsToFinish < 0) {
						t.cancel();
						setValue(JOptionPane.OK_OPTION);
					}
				} catch (Exception e) {
					t.cancel();
					setValue(null);
				}
			}
		};

		t.schedule(showDialogTask, delay);
		t.schedule(updateDialogMessageTask, delay + 1000, 1000);
	}

	public static interface CallBack {
		public void process(int result);
	}
}
