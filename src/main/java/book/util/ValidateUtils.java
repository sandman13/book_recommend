package book.util;

import book.domain.exception.BusinessException;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public class ValidateUtils {

    /**
     * 检验对象是否为空
     *
     * @param object
     * @param errorMessage
     * @param args
     */
    public static void checkNotNull(Object object, String errorMessage, Object... args) {
        if (object == null) {
            throw new BusinessException(MessageFormat.format(errorMessage, args));
        }
    }

    /**
     * 校验集合对象是否为空(包含集合为空和集合大小为空)
     *
     * @param collection
     * @param errorMessage
     * @param args
     */
    public static void checkNotEmpty(Collection collection, String errorMessage,
                                     Object... args) {
        if (collection == null || collection.size() == 0) {
            throw new BusinessException(MessageFormat.format(errorMessage, args));
        }
    }

    /**
     * 校验字符串对象是否为空(包含字符串对象和字符串大小)
     *
     * @param string
     * @param errorMessage
     * @param args
     */
    public static void checkNotEmpty(String string, String errorMessage,
                                     Object... args) {
        if (string == null || string.length() == 0) {
            throw new BusinessException(MessageFormat.format(errorMessage, args));
        }
    }

    /**
     * 校验表达式是否为真
     *
     * @param expression
     * @param errorMessage
     * @param args
     */
    public static void checkTrue(boolean expression, String errorMessage,
                                 Object... args) {
        if (!expression) {
            throw new BusinessException(MessageFormat.format(errorMessage, args));
        }
    }
}
