package co.zpdev.core.discord.logger;


import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * The logger.
 *
 * @author zpdev
 * @version 0.9_BETA
 * @deprecated until future update.
 */
public class ZLogger {

    private static Logger logger;

    public static void initialise() {
        try {
            logger = Logger.getLogger("GBVerify");
            ConsoleHandler cHandler = new ConsoleHandler();
            cHandler.setFormatter(new ZFormatter());
            logger.addHandler(cHandler);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        ZLogger.blankLine();
    }

    public static void info(String message) {
        logger.info(message + "\n");
    }

    public static void warn(String message) {
        logger.warning(message + "\n");
    }

    public static void err(String message) {
        logger.log(new CustomLevel("ERR", 1), message + "\n");
    }

    public static void debug(String message) {
        logger.log(new CustomLevel("DEBUG", 2), message + "\n");
    }

    private static void blankLine() {
        System.out.println();
    }

}
