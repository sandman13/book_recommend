package book.dao;

import book.domain.dataobject.UserDO;

import java.util.List;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public interface UserDao {

     List<UserDO> queryByUserNameAndPassword(String username, String password);

     UserDO queryByUserId(long userId);

     List<UserDO> listAllUsers();

     UserDO queryByName(String username);

     long insertUser(UserDO userDO);

     long updateUser(UserDO userDO);

     long deleteUser(long userId);
}
