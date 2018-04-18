package book.util;

import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public class ExceptionHandler {

    private static final String errorMessage = "系统繁忙,请稍后重试";

    /**
     * 拦截类似权限不足,名称重复等自定义商业异常,并提供给前台错误信息
     *
     * @param LOGGER
     * @param result
     * @param ex
     * @param message
     * @param args
     */
    public static void handleBusinessException(Logger LOGGER, BaseResult result, BusinessException ex, String message, Object... args) {
        result.setSuccess(false);
        result.setMessage(ex.getMessage());
        LOGGER.error(MessageFormat.format(message, args), ex);
    }

    /**
     * 拦截其余运行时异常和所有的非运行时异常,不提供给前台系统内部错误
     *
     * @param LOGGER
     * @param result
     * @param ex
     * @param message
     * @param args
     */
    public static void handleSystemException(Logger LOGGER, BaseResult result, Exception ex, String message, Object... args) {
        result.setSuccess(false);
        result.setMessage(errorMessage);
        LOGGER.error(MessageFormat.format(message, args), ex);
    }
}
