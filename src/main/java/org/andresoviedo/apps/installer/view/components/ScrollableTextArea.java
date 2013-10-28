package org.andresoviedo.apps.installer.view.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.andresoviedo.apps.installer.model.Model;


@SuppressWarnings("serial")
public class ScrollableTextArea extends JScrollPane {

	private Model model;
	private JTextArea console;

	public ScrollableTextArea(Model model) {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.model = model;
		console = new JTextArea();
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setEditable(false);
		console.setFont(new Font("Monospaced", Font.PLAIN, 12));
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		console.setCaretColor(Color.WHITE);
		setViewportView(console);
		refreshStatus();
	}

	public void refreshStatus() {
		synchronized (model.getLoggerMessages()) {
			String text = model.getLoggerMessages().toString();
			console.setText(text);
			console.setCaretPosition(text.length());
		}
	}

	public void showCursor(Cursor cursor) {
		console.setCursor(cursor);
	}
}
