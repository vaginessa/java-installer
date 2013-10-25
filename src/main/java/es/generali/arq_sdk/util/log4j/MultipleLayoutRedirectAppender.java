package es.generali.arq_sdk.util.log4j;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

public final class MultipleLayoutRedirectAppender extends AppenderSkeleton {

	private MultipleLayoutRollingFileAppender appender;

	@Override
	public void setName(String name) {
		super.setName(name);
		String[] params = name.split("-");
		if (params.length < 3)
			throw new IllegalArgumentException(
					"El formato del logger es 'X-Y-ZZZZ'; donde 'X' es el nombre del logger con el 'Y' appender asociado. 'Z' es el identificador de este appender");
		Log targetLogger = LogFactory.getLog(params[0]);
		if (targetLogger == null) {
			throw new IllegalArgumentException("El logger '" + params[0]
					+ "' no existe");
		}
		appender = (MultipleLayoutRollingFileAppender) Logger.getLogger(
				params[0]).getAppender(params[1]);
		if (appender == null) {
			throw new IllegalArgumentException("El logger '" + params[0]
					+ "' no tiene el appender '" + appender + "'");
		}
	}

	@Override
	public void close() {

	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent event) {
		appender.subAppend(event, this.layout);
	}

	public static class MultipleLayoutRollingFileAppender extends FileAppender {

		/**
		 * The default maximum file size is 10MB.
		 */
		protected long maxFileSize = 10 * 1024 * 1024;

		/**
		 * There is one backup file by default.
		 */
		protected int maxBackupIndex = 1;

		private long nextRollover = 0;

		/**
		 * The default constructor simply calls its
		 * {@link FileAppender#FileAppender parents constructor}.
		 */
		public MultipleLayoutRollingFileAppender() {
			super();
		}

		/**
		 * Instantiate a RollingFileAppender and open the file designated by
		 * <code>filename</code>. The opened filename will become the ouput
		 * destination for this appender.
		 * 
		 * <p>
		 * If the <code>append</code> parameter is true, the file will be
		 * appended to. Otherwise, the file desginated by <code>filename</code>
		 * will be truncated before being opened.
		 */
		public MultipleLayoutRollingFileAppender(Layout layout,
				String filename, boolean append) throws IOException {
			super(layout, filename, append);
		}

		/**
		 * Instantiate a FileAppender and open the file designated by
		 * <code>filename</code>. The opened filename will become the output
		 * destination for this appender.
		 * 
		 * <p>
		 * The file will be appended to.
		 */
		public MultipleLayoutRollingFileAppender(Layout layout, String filename)
				throws IOException {
			super(layout, filename);
		}

		/**
		 * Returns the value of the <b>MaxBackupIndex</b> option.
		 */
		public int getMaxBackupIndex() {
			return maxBackupIndex;
		}

		/**
		 * Get the maximum size that the output file is allowed to reach before
		 * being rolled over to backup files.
		 * 
		 * @since 1.1
		 */
		public long getMaximumFileSize() {
			return maxFileSize;
		}

		/**
		 * Implements the usual roll over behaviour.
		 * 
		 * <p>
		 * If <code>MaxBackupIndex</code> is positive, then files {
		 * <code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code> are
		 * renamed to {<code>File.2</code>, ...,
		 * <code>File.MaxBackupIndex</code> . Moreover, <code>File</code> is
		 * renamed <code>File.1</code> and closed. A new <code>File</code> is
		 * created to receive further log output.
		 * 
		 * <p>
		 * If <code>MaxBackupIndex</code> is equal to zero, then the
		 * <code>File</code> is truncated with no backup files created.
		 */
		public// synchronization not necessary since doAppend is alreasy synched
		void rollOver() {
			File target;
			File file;

			if (qw != null) {
				long size = ((CountingQuietWriter) qw).getCount();
				LogLog.debug("rolling over count=" + size);
				// if operation fails, do not roll again until
				// maxFileSize more bytes are written
				nextRollover = size + maxFileSize;
			}
			LogLog.debug("maxBackupIndex=" + maxBackupIndex);

			boolean renameSucceeded = true;
			// If maxBackups <= 0, then there is no file renaming to be done.
			if (maxBackupIndex > 0) {
				// Delete the oldest file, to keep Windows happy.
				file = new File(fileName + '.' + maxBackupIndex);
				if (file.exists())
					renameSucceeded = file.delete();

				// Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex,
				// ..., 3,
				// 2}
				for (int i = maxBackupIndex - 1; i >= 1 && renameSucceeded; i--) {
					file = new File(fileName + "." + i);
					if (file.exists()) {
						target = new File(fileName + '.' + (i + 1));
						LogLog.debug("Renaming file " + file + " to " + target);
						renameSucceeded = file.renameTo(target);
					}
				}

				if (renameSucceeded) {
					// Rename fileName to fileName.1
					target = new File(fileName + "." + 1);

					this.closeFile(); // keep windows happy.

					file = new File(fileName);
					LogLog.debug("Renaming file " + file + " to " + target);
					renameSucceeded = file.renameTo(target);
					//
					// if file rename failed, reopen file with append = true
					//
					if (!renameSucceeded) {
						try {
							this.setFile(fileName, true, bufferedIO, bufferSize);
						} catch (IOException e) {
							if (e instanceof InterruptedIOException) {
								Thread.currentThread().interrupt();
							}
							LogLog.error("setFile(" + fileName
									+ ", true) call failed.", e);
						}
					}
				}
			}

			//
			// if all renames were successful, then
			//
			if (renameSucceeded) {
				try {
					// This will also close the file. This is OK since multiple
					// close operations are safe.
					this.setFile(fileName, false, bufferedIO, bufferSize);
					nextRollover = 0;
				} catch (IOException e) {
					if (e instanceof InterruptedIOException) {
						Thread.currentThread().interrupt();
					}
					LogLog.error("setFile(" + fileName
							+ ", false) call failed.", e);
				}
			}
		}

		public synchronized void setFile(String fileName, boolean append,
				boolean bufferedIO, int bufferSize) throws IOException {
			super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
			if (append) {
				File f = new File(fileName);
				((CountingQuietWriter) qw).setCount(f.length());
			}
		}

		/**
		 * Set the maximum number of backup files to keep around.
		 * 
		 * <p>
		 * The <b>MaxBackupIndex</b> option determines how many backup files are
		 * kept before the oldest is erased. This option takes a positive
		 * integer value. If set to zero, then there will be no backup files and
		 * the log file will be truncated when it reaches
		 * <code>MaxFileSize</code>.
		 */
		public void setMaxBackupIndex(int maxBackups) {
			this.maxBackupIndex = maxBackups;
		}

		/**
		 * Set the maximum size that the output file is allowed to reach before
		 * being rolled over to backup files.
		 * 
		 * <p>
		 * This method is equivalent to {@link #setMaxFileSize} except that it
		 * is required for differentiating the setter taking a <code>long</code>
		 * argument from the setter taking a <code>String</code> argument by the
		 * JavaBeans {@link java.beans.Introspector Introspector}.
		 * 
		 * @see #setMaxFileSize(String)
		 */
		public void setMaximumFileSize(long maxFileSize) {
			this.maxFileSize = maxFileSize;
		}

		/**
		 * Set the maximum size that the output file is allowed to reach before
		 * being rolled over to backup files.
		 * 
		 * <p>
		 * In configuration files, the <b>MaxFileSize</b> option takes an long
		 * integer in the range 0 - 2^63. You can specify the value with the
		 * suffixes "KB", "MB" or "GB" so that the integer is interpreted being
		 * expressed respectively in kilobytes, megabytes or gigabytes. For
		 * example, the value "10KB" will be interpreted as 10240.
		 */
		public void setMaxFileSize(String value) {
			maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
		}

		protected void setQWForFiles(Writer writer) {
			this.qw = new CountingQuietWriter(writer, errorHandler);
		}

		/**
		 * This method differentiates RollingFileAppender from its super class.
		 * 
		 * @since 0.9.0
		 */
		protected void subAppend(LoggingEvent event, Layout layout) {
			this.qw.write(layout.format(event));

			if (layout.ignoresThrowable()) {
				String[] s = event.getThrowableStrRep();
				if (s != null) {
					int len = s.length;
					for (int i = 0; i < len; i++) {
						this.qw.write(s[i]);
						this.qw.write(Layout.LINE_SEP);
					}
				}
			}

			if (shouldFlush(event)) {
				this.qw.flush();
			}
			if (fileName != null && qw != null) {
				long size = ((CountingQuietWriter) qw).getCount();
				if (size >= maxFileSize && size >= nextRollover) {
					rollOver();
				}
			}
		}

	}

}
