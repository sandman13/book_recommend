package book.domain.dto;

import book.domain.BaseObject;

/**
 * @author hui zhang
 * @date 2018/3/22
 */
public class RecommendDTO extends BaseObject{
    private long recommendId;

    private long userId;
    private String bookName;

    private String author;

    private String gmtCreate;

    private String gmtModifier;

    private String modifier;

    private int age;

    private int sex;

    private String profession;

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

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModifier() {
        return gmtModifier;
    }

    public void setGmtModifier(String gmtModifier) {
        this.gmtModifier = gmtModifier;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
