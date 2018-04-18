package book.util;

import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public class LoggerUtil {

    public static void info(Logger logger, String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(format(message, params));
        }
    }


    public static void info(Throwable throwable, Logger logger, String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(format(message, params), throwable);
        }
    }


    public static void warn(Logger logger, String message, Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(message, params));
        }
    }


    public static void warn(Throwable throwable, Logger logger, String message, Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(message, params), throwable);
        }
    }


    public static void debug(Logger logger, String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(message, params));
        }
    }


    public static void debug(Throwable throwable, Logger logger, String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(message, params), throwable);
        }
    }


    public static void error(Logger logger, String message, Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error(format(message, params));
        }
    }


    public static void error(Throwable throwable, Logger logger, String message, Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error(format(message, params), throwable);
        }
    }


    private static String format(String message, Object... params) {
        if (params != null && params.length != 0) {
            return MessageFormat.format(message, params);
        }
        return "";
    }
}
