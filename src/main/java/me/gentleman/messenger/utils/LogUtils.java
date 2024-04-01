package me.gentleman.messenger.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * If you need javadocs for this then rip
 */
public class LogUtils {
    public static void log(String msg) {
        Logger.getGlobal().log(Level.INFO, msg);
    }
    public static void debug(String msg) {
        Logger.getGlobal().log(Level.FINER, msg);
    }
    public static void warn(String msg) {
        Logger.getGlobal().log(Level.WARNING, msg);
    }
    public static void error(String msg) {
        Logger.getGlobal().log(Level.SEVERE, msg);
    }
}
