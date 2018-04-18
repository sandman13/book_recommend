package book.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 基础领域模型
 * 封装序列化和toString方法
 * @author hui zhang
 * @date 2018/3/20
 */
public class BaseObject implements Serializable {

    /**
     * 这里指定哈希字段,便于当结构修改后反序列化仍然不抛出异常
     */
    private static final long serialVersionUID = 39400076738350434L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}