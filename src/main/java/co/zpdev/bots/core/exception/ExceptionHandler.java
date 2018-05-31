package co.zpdev.bots.core.exception;

import co.zpdev.bots.core.logger.ZLogger;
import co.zpdev.bots.core.util.PasteUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * The exception handler.
 *
 * @author zpdev
 * @version 0.9_BETA
 * @deprecated until future update.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        ZLogger.err("Encountered an exception! Sending stacktrace...");
        ZLogger.err("Stacktrace: " + PasteUtil.paste(getStackTrace(e)));
    }

    public static void handleException(String issue, Exception e) {
        ZLogger.err("Encountered an exception! Error " + issue);
        ZLogger.err("Stacktrace: " + PasteUtil.paste(getStackTrace(e)));
    }

    private static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

}
