package org.andresoviedo.apps.installer.services;

import org.andresoviedo.apps.installer.model.FileSystem;

public class FileSystemMonitorService {

	private FileSystem model;

	// Filesystem monitor service
	private int eclipse_fs_monitor;

	public FileSystemMonitorService(FileSystem model) {
		this.model = model;
//		init();
	}

//	public void init() {
//		try {
//			int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED
//					| JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
//			eclipse_fs_monitor = JNotify.addWatch(
//					Model.ARQHOME.getAbsolutePath(), mask, false, model);
//		} catch (JNotifyException ex) {
//			throw new RuntimeException(
//					"Exception initializing file system monitor.", ex);
//		}
//	}
//
//	public void close() {
//		try {
//			JNotify.removeWatch(eclipse_fs_monitor);
//		} catch (JNotifyException ex) {
//			throw new RuntimeException(
//					"Exception initializing file system monitor.", ex);
//		}
//	}
}