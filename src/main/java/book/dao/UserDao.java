package book.dao;

import book.domain.dataobject.UserDO;

import java.util.List;

/**
 * @author yutong song
 * @date 2018/3/20
 */
public interface UserDao {

     List<UserDO> queryByUserNameAndPassword(String usernmae, String password);

     UserDO queryByUserId(long userId);

     List<UserDO> listAllUsers();
}
