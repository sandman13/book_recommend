package book.domain.exception;

/**
 * @author hui zhang
 * @date 2018/3/20
 */

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 自定义异常
 * 用于返回例如名称重复,权限不够等商业异常,并被facade层捕获塞到返回result对象中
 * 其余的运行时异常或者所有的非运行时异常都直接返回请求失败
 */
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 3243377604765079331L;

    public BusinessException() {

    }

    public BusinessException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
