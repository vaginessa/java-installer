package es.generali.arq_sdk.util.run;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.generali.arq_sdk.model.Model;
import es.generali.arq_sdk.util.io.IOHelper.StreamGobbler;

public final class RunHelper {

	private static final Log logger = LogFactory.getLog(Model.class);

	public static int exec(String file, Appendable output, String... args)
			throws Exception {
		String[] cmdArray = new String[args != null ? args.length + 1 : 1];
		cmdArray[0] = file;
		if (args != null) {
			System.arraycopy(args, 0, cmdArray, 1, args.length);
		}
		return exec(cmdArray, output);
	}

	public static int execBat(String batFile, Appendable output, String... args)
			throws Exception {
		String[] cmdArray = new String[3];
		cmdArray[0] = "cmd.exe";
		cmdArray[1] = "/c";
		cmdArray[2] = "\"";
		cmdArray[2] += "\"" + batFile + "\"";
		if (args != null) {
			for (String arg : args) {
				if (arg.contains(" ")) {
					cmdArray[2] += " \"" + arg + "\"";
				} else {
					cmdArray[2] += " " + arg;
				}
			}
		}
		cmdArray[2] += "\"";
		return exec(cmdArray, output);
	}

	public static int exec(String[] cmdArray, Appendable output)
			throws Exception {
		logger.info("Executing " + Arrays.toString(cmdArray) + "'...");

		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(cmdArray);

		StreamGobbler stdOutReader = new StreamGobbler(pr.getInputStream(),
				"STDOUT", output);
		StreamGobbler stdErrReader = new StreamGobbler(pr.getErrorStream(),
				"STDERR");

		stdOutReader.start();
		stdErrReader.start();

		int status = pr.waitFor();

		logger.info("Finished with status '" + status + "'.");
		return status;
	}
}
