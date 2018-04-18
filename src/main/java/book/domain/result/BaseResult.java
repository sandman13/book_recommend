package book.domain.result;

import book.domain.BaseObject;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public class BaseResult extends BaseObject {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 返回信息
     */
    private String message;

    public BaseResult() {
        this(false);
    }

    public BaseResult(boolean success) {
        this(success, null);
    }

    public BaseResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
