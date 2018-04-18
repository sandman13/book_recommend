package book.domain.Enum;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public enum StatusEnum {

    DELETED("被删除"),

    ADMIN("管理员"),

    READER("普通用户"),

    CAN_BORROW("可借阅"),

    HAS_BORROW("已借出"),

    VALID("有效的");

    private String description;

    StatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
