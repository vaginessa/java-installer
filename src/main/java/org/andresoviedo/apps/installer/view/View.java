package org.andresoviedo.apps.installer.view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventListener;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.andresoviedo.apps.installer.Main;
import org.andresoviedo.apps.installer.controller.Controller;
import org.andresoviedo.apps.installer.model.Model;
import org.andresoviedo.apps.installer.view.components.ScrollableTextArea;
import org.andresoviedo.apps.installer.view.components.TimeoutOptionPane;
import org.andresoviedo.apps.installer.view.components.TimeoutOptionPane.CallBack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class View extends JFrame implements Observer, EventListener {

	private static final String WINDOW_TITLE = "ARQ-SDK Installer";

	private Log logger = LogFactory.getLog(View.class);

	private final Controller controller;
	private final Model model;

	private InstallAllAction installAllAction = new InstallAllAction();
	private UninstallAllAction uninstallAllAction = new UninstallAllAction();

	private ScrollableTextArea scrollableTextArea;

	private Worker<?> currentWorker;

	public View(Controller controller, Model model) {
		this.controller = controller;
		this.model = model;
		init();
	}

	private void init() {
		// Init User Interface
		initGUI();

		// Init timer for unattended install
		// initTimeout();
	}

	private void initGUI() {
		// configure GUI
		initWindow();
		initTitle();
		// addVGap();
		initConsole();
		// addVGap();
		// initComponents();
		pack();
	}

	private void initWindow() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (currentWorker != null) {
					if (JOptionPane
							.showConfirmDialog(
									View.this,
									"There are currently processes running. Are you sure you want to quit?",
									WINDOW_TITLE, JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
						return;
					}
//					model.killProcesses();
				}
				logger.debug("Exiting application...");
				Main.exit();
			}
		});
		setContentPane(new JPanel());
		BoxLayout layoutMgr = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		getContentPane().setLayout(layoutMgr);
		setTitle(WINDOW_TITLE);
	}

	private void addVGap() {
		getContentPane().add(Box.createRigidArea(new Dimension(10, 10)));
	}

	private void initTitle() {
		ImageIcon icon = new ImageIcon(this.getClass().getResource(
				"images/title.png"));
		JLabel label1 = new JLabel(icon);
		JPanel jPanelIcon = new JPanel();
		jPanelIcon.setLayout(new BoxLayout(jPanelIcon, BoxLayout.X_AXIS));
		jPanelIcon.add(label1);

		getContentPane().add(jPanelIcon);
	}

	// private void initComponents() {
	//
	// // List of components
	// GridLayout componentsLayout = new GridLayout(0, 3);
	// componentsLayout.setHgap(10);
	// componentsLayout.setVgap(10);
	// JPanel componentsPanel = new JPanel(componentsLayout);
	// getContentPane().add(componentsPanel);
	//
	// // Unnattended install
	// componentsPanel.add(new JLabel("ARQ-SDK"));
	// installAllAction = new InstallAllAction();
	// componentsPanel.add(new JButton(installAllAction));
	// uninstallAllAction = new UninstallAllAction();
	// componentsPanel.add(new JButton(uninstallAllAction));
	//
	// // Eclipse
	// componentsPanel.add(new JLabel("eclipse"));
	// installEclipseAction = new InstallEclipseAction();
	// componentsPanel.add(new JButton(installEclipseAction));
	// uninstallEclipseAction = new UninstallEclipseAction();
	// componentsPanel.add(new JButton(uninstallEclipseAction));
	//
	// componentsPanel.add(new JLabel("eclipse workspace"));
	// installWorkspaceAction = new InstallEclipseWorkspaceAction();
	// componentsPanel.add(new JButton(installWorkspaceAction));
	// uninstallEclipseWorkspaceAction = new UninstallEclipseWorkspaceAction();
	// componentsPanel.add(new JButton(uninstallEclipseWorkspaceAction));
	// }

	private void initComponents() {

		// List of components
		JPanel componentsPanel = new JPanel();
		new BoxLayout(componentsPanel, BoxLayout.X_AXIS);
		getContentPane().add(componentsPanel);

		// Unnattended install

		JButton installButton = new JButton(installAllAction);
		installButton.setPreferredSize(new Dimension(128, 32));
		componentsPanel.add(installButton);

		JButton uninstallButton = new JButton(uninstallAllAction);
		uninstallButton.setPreferredSize(new Dimension(128, 32));
		componentsPanel.add(uninstallButton);
	}

	private void initConsole() {
		JPanel consolePanel = new JPanel();
		consolePanel.setLayout(new BoxLayout(consolePanel, BoxLayout.Y_AXIS));

		// consolePanel.add(new JLabel("Consola"));
		scrollableTextArea = new ScrollableTextArea(model);
		scrollableTextArea.setPreferredSize(new Dimension(640, 300));
		consolePanel.add(scrollableTextArea);
		getContentPane().add(consolePanel);
	}

	// /**
	// * Diálogo con timeout que al finalizar arranca la instalación desatendida
	// */
	// private void initTimeout() {
	// new TimeoutOptionPane(this, "Starting install in {0} seconds...",
	// WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE,
	// JOptionPane.CANCEL_OPTION, 1000, 5, new CallBack() {
	// @Override
	// public void process(int result) {
	// if (result == JOptionPane.CANCEL_OPTION
	// || result == JOptionPane.CLOSED_OPTION) {
	// logger.info("Unattended install aborted.");
	// return;
	// }
	// installAllAction.actionPerformed(new ActionEvent(
	// TimeoutOptionPane.class, -1, "unattended"));
	// }
	// });
	// logger.info("Starting unattended install...");
	// }

	public void installDelayed() {
		new TimeoutOptionPane(this, "Starting install in {0} seconds...",
				WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.CANCEL_OPTION, 1000, 30, new CallBack() {
					@Override
					public void process(int result) {
						if (result == JOptionPane.CANCEL_OPTION
								|| result == JOptionPane.CLOSED_OPTION) {
							logger.info("Unattended install aborted.");
							Main.exit();
							return;
						}
						installAllAction.actionPerformed(new ActionEvent(
								TimeoutOptionPane.class, -1, "unattended"));
					}
				});
		logger.info("Starting unattended install...");
	}

	public void uninstallDelayed() {
		new TimeoutOptionPane(this, "Starting uninstall in {0} seconds...",
				WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.CANCEL_OPTION, 1000, 8, new CallBack() {
					@Override
					public void process(int result) {
						if (result == JOptionPane.CANCEL_OPTION
								|| result == JOptionPane.CLOSED_OPTION) {
							logger.info("Unattended uninstall aborted.");
							Main.exit();
							return;
						}
						uninstallAllAction.actionPerformed(new ActionEvent(
								View.class, -1, "unattended"));
					}
				});
		logger.info("Starting unattended uninstall...");
	}

	private boolean isExecutionAllowed() {
		// return !model.areProcessesRunning()
		// && (currentWorker == null || currentWorker.isDone());
		return true;
	}

	@Override
	public void update(Observable o, Object arg) {
		if ("logging".equals(arg)) {
			scrollableTextArea.refreshStatus();
		} else {
			installAllAction.refreshStatus();
			uninstallAllAction.refreshStatus();
			// installEclipseAction.refreshStatus();
			// uninstallEclipseAction.refreshStatus();
			// installWorkspaceAction.refreshStatus();
			// uninstallEclipseWorkspaceAction.refreshStatus();
		}

	}

	private void showMessageDialog(String msg, int msgType) {
		JOptionPane.showMessageDialog(this, msg, WINDOW_TITLE, msgType);
	}

	class InstallAllAction extends AbstractAction {

		public InstallAllAction() {
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			currentWorker = new Worker<Integer>(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					try {
						if ("unattended".equals(e.getActionCommand())
								|| JOptionPane.showConfirmDialog(View.this,
										"Install ARQ-SDK?", WINDOW_TITLE,
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
							int status = controller.installAll();
							if ("unattended".equals(e.getActionCommand())) {
								Main.exit();
							}
							return status;
						} else {
							logger.info("Installation aborted");
							return 0;
						}
					} catch (Exception ex) {
						logger.fatal(ex);
						showMessageDialog(
								"Unexpected exception. Please review log.",
								JOptionPane.ERROR_MESSAGE);
						Main.exit();
						throw ex;
					}
				}
			});
			currentWorker.execute();
			View.this.update(null, null);
		}

		void refreshStatus() {
			// Aqui se puede cambiar el label Reinstall/Install según estado de
			// la máquina
			putValue(Action.NAME, controller.isAnythingInstalled() ? "Install"
					: "Install");
			setEnabled(isExecutionAllowed());
		}

	}

	class Worker<T> extends SwingWorker<T, Void> {

		private Callable<T> command;

		public Worker(Callable<T> command) {
			this.command = command;
		}

		@Override
		protected T doInBackground() throws Exception {
			View.this.scrollableTextArea.setCursor(Cursor
					.getPredefinedCursor(Cursor.WAIT_CURSOR));
			return command.call();
		}

		@Override
		protected void done() {
			View.this.scrollableTextArea.setCursor(Cursor
					.getPredefinedCursor(Cursor.TEXT_CURSOR));
			View.this.currentWorker = null;
			View.this.update(null, "worker_done");
		}
	}

	class UninstallAllAction extends AbstractAction {

		public UninstallAllAction() {
			super("Uninstall");
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			currentWorker = new Worker<Integer>(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					try {
						if ("unattended".equals(e.getActionCommand())
								|| JOptionPane.showConfirmDialog(View.this,
										"Uninstall ARQ-SDK?", WINDOW_TITLE,
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
							int status = controller.uninstallAll();
							if ("unattended".equals(e.getActionCommand())) {
								Main.exit();
							}
							return status;
						} else {
							logger.info("Uninstallation aborted");
							return 0;
						}
					} catch (Exception ex) {
						logger.fatal(ex);
						showMessageDialog(
								"Unexpected exception. Please review log.",
								JOptionPane.ERROR_MESSAGE);
						Main.exit();
						throw ex;
					}
				}
			});
			currentWorker.execute();
			View.this.update(null, null);
		}

		void refreshStatus() {
			setEnabled(isExecutionAllowed());
		}
	}
}