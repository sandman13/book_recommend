package book.domain.result;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public class SimpleResult<T> extends BaseResult {

    /**
     * 具体返回的对象
     */
    private T result;

    public SimpleResult(T result) {
        super();
        this.result = result;
    }

    public SimpleResult() {

    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
