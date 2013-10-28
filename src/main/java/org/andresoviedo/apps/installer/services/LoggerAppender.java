package org.andresoviedo.apps.installer.services;

import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.apps.installer.Main;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


public class LoggerAppender extends AppenderSkeleton {

	private List<LoggingEvent> pendingLogsToProcess = new ArrayList<LoggingEvent>();

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected synchronized void append(LoggingEvent event) {
		if (Main.getModel() != null) {
			if (pendingLogsToProcess.isEmpty()) {
				append_impl(event);
			} else {
				for (LoggingEvent oldEvent : pendingLogsToProcess) {
					append_impl(oldEvent);
				}
				pendingLogsToProcess.clear();
			}
		} else {
			pendingLogsToProcess.add(event);
		}
	}

	private void append_impl(LoggingEvent event) {
		Main.getModel().processLoggingEvent(getLayout().format(event));
		if (event.getThrowableStrRep() != null) {
			for (String exEvent : event.getThrowableStrRep()) {
				Main.getModel().processLoggingEvent(exEvent + "\n");
			}
		}
	}
}
