package com.zp4rker.core.discord.exception;

import com.zp4rker.core.discord.util.PostUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Uncaught and caught exception handler.
 *
 * @author zpdev
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static String name = "";

    /**
     * Initialises the handler.
     *
     * @param token the pushbullet token to use
     * @param name the instance name to use
     */
    public static void init(String token, String name) {
        PostUtil.init(token);
        ExceptionHandler.name = name;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handleException("unexpected", e);
    }

    /**
     * Handles an exception. If token not present, print to console.
     *
     * @param issue when the exception occured
     * @param e the error it threw
     */
    public static void handleException(String issue, Throwable e) {
        try {
            String paste = PostUtil.paste(getStackTrace(e));
            PostUtil.push(name, "Encountered an exception when " + issue + ".\n" + paste);
        } catch (IllegalStateException ex) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a stacktrace to a string.
     *
     * @param t the throwable
     * @return the parsed string
     */
    private static String getStackTrace(Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        return result.toString();
    }

}
