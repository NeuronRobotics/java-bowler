package com.neuronrobotics.sdk.util;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class FileChangeWatcher.
 */
public class FileChangeWatcher extends Thread {

	/** The file to watch. */
	private File fileToWatch;
	
	/** The run. */
	private boolean run = true;
	
	/** The watcher. */
	private final WatchService watcher;
	
	/** The keys. */
	private final Map<WatchKey, Path> keys;
	
	/** The recursive. */
	private final boolean recursive=false;
	
	/** The listeners. */
	private ArrayList<IFileChangeListener> listeners = new ArrayList<IFileChangeListener>();
	
	/**
	 * Instantiates a new file change watcher.
	 *
	 * @param fileToWatch the file to watch
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public FileChangeWatcher(File fileToWatch) throws IOException {
		this.setFileToWatch(fileToWatch);
		setName("File Watcher Thread for " + fileToWatch.getAbsolutePath());
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		Path dir = Paths.get(fileToWatch.getParent());
		if (recursive) {
			System.out.format("Scanning %s ...\n", dir);
			registerAll(dir);
			System.out.println("Done.");
		} else {
			register(dir);
		}
	}
	
	/**
	 * Adds the i file change listener.
	 *
	 * @param l the l
	 */
	public void addIFileChangeListener(IFileChangeListener l){
		if(!listeners.contains(l)){
			listeners.add(l);
		}
	}
	
	/**
	 * Removes the i file change listener.
	 *
	 * @param l the l
	 */
	public void removeIFileChangeListener(IFileChangeListener l){
		if(listeners.contains(l)){
			listeners.remove(l);
		}
	}

	/**
	 * Cast.
	 *
	 * @param <T> the generic type
	 * @param event the event
	 * @return the watch event
	 */
	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService.
	 *
	 * @param dir the dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,ENTRY_MODIFY);

		Path prev = keys.get(key);
		if (prev == null) {
			//System.out.format("register: %s\n", dir);
		} else {
			if (!dir.equals(prev)) {
				//System.out.format("update: %s -> %s\n", prev, dir);
			}
		}
		
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 *
	 * @param start the start
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (run) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				//System.out.format("%s: %s\n", event.kind().name(), child);
				for(IFileChangeListener l: listeners){
					l.onFileChange(child.toFile(), event);
				}

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readbale
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	/**
	 * Gets the file to watch.
	 *
	 * @return the file to watch
	 */
	public File getFileToWatch() {
		return fileToWatch;
	}

	/**
	 * Sets the file to watch.
	 *
	 * @param fileToWatch the new file to watch
	 */
	public void setFileToWatch(File fileToWatch) {
		this.fileToWatch = fileToWatch;
	}

	/**
	 * Checks if is run.
	 *
	 * @return true, if is run
	 */
	public boolean isRun() {
		return run;
	}

	/**
	 * Close.
	 */
	public void close() {
		this.run = false;

	}

}
