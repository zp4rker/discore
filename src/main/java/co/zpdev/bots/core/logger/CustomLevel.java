package co.zpdev.bots.core.logger;

import java.util.logging.Level;

/**
 * The custom level type for ZLogger.
 *
 * @author zpdev
 * @deprecated until future update.
 */
class CustomLevel extends Level {

    CustomLevel(String name, int value) {
        super(name, Level.SEVERE.intValue() + value);
    }

}
