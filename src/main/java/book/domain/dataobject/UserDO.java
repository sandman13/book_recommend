package book.domain.dataobject;

import book.domain.BaseObject;

import java.util.Date;

/**
 * @author yutong song
 * @date 2018/3/20
 */
public class UserDO extends BaseObject{

    private long userId;

    private String username;

    private String password;

    /**
     * @see book.domain.Enum.StatusEnum
     * 用户状态
     */
    private String userStatus;

    private Date gmtCreate;

    private Date gmtModified;

    private String modifier;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
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
}
