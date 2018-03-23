package book.domain.dataobject;

import org.apache.commons.lang.StringUtils;

/**
 * @author yutong song
 * @date 2018/3/22
 */
public class RecommendDO {

    private long userId;

    /**
     * 这里的bookId
     */
    private String bookId;

    private int rate;


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecommendDO) {
            return StringUtils.equals(this.bookId, ((RecommendDO) obj).bookId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getBookId().hashCode();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
