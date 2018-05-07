package book.domain.dataobject;

import book.domain.BaseObject;

import java.util.Date;

/**
 * @author hui zhang
 * @date 2018/3/22
 */
public class RecommendationDO extends BaseObject {

    private long recommendId;

    private long userId;

    private String bookName;

    private String author;

    private Date gmtCreate;

    private Date gmtModified;

    private String modifier;

    private int recommendType;

    public long getRecommendId() {
        return recommendId;
    }

    public void setRecommendId(long recommendId) {
        this.recommendId = recommendId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }


    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public int getRecommendType() {
        return recommendType;
    }

    public void setRecommendType(int recommendType) {
        this.recommendType = recommendType;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
