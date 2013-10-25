package es.generali.arq_sdk.model;

import java.util.Observable;

import net.contentobjects.jnotify.JNotifyListener;

public class FileSystem extends Observable implements JNotifyListener {

	public FileSystem() {
	}

	@Override
	public void fileRenamed(int wd, String rootPath, String oldName,
			String newName) {
		notify("renamed " + rootPath + " : " + oldName + " -> " + newName,
				rootPath, oldName, newName);
	}

	@Override
	public void fileModified(int wd, String rootPath, String name) {
		notify("modified " + rootPath + " : " + name, rootPath, name);
	}

	@Override
	public void fileDeleted(int wd, String rootPath, String name) {
		notify("deleted " + rootPath + " : " + name, rootPath, name);
	}

	@Override
	public void fileCreated(int wd, String rootPath, String name) {
		notify("created " + rootPath + " : " + name, rootPath, name);
	}

	private void notify(String msg, String rootPath, String name) {
		setChanged();
		notifyObservers();
	}

	private void notify(String msg, String rootPath, String oldName,
			String newName) {
		setChanged();
		notifyObservers();
	}
}