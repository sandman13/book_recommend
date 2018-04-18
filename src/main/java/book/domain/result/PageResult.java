package book.domain.result;

import java.util.List;

/**
 * @author hui zhang
 * @date 2018/4/16
 */
public class PageResult<T> extends BaseResult {

    private List<T> result;

    /**
     * 当前页数
     */
    private int pageNumber;

    /**
     * 当前页最大数量
     */
    private int pageSize;

    /**
     * 第一页
     */
    private int firstPage;

    /**
     * 最后一页
     */
    private int lastPage;

    /**
     * 前一页
     */
    private int prePage;

    /**
     * 后一页
     */
    private int nextPage;

    /**
     * 有前一页
     */
    private boolean hasPrePage;

    /**
     * 有后一页
     */
    private boolean hasNextPage;

    public PageResult(int pageNumber, int pageSize, int firstPage, int lastPage, int prePage, int nextPage, boolean hasPrePage, boolean hasNextPage) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.prePage = prePage;
        this.nextPage = nextPage;
        this.hasPrePage = hasPrePage;
        this.hasNextPage = hasNextPage;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isHasPrePage() {
        return hasPrePage;
    }

    public void setHasPrePage(boolean hasPrePage) {
        this.hasPrePage = hasPrePage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
