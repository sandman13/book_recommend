package book.dao.impl;

import book.dao.CommonDao;
import book.dao.UserDao;
import book.domain.dataobject.UserDO;
import book.util.LoggerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yutong song
 * @date 2018/3/20
 */
@Repository(value = "userDao")
public class UserDaoImpl extends CommonDao implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    /**
     * 根据用户名和密码返回用户信息
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public List<UserDO> queryByUserNameAndPassword(String username, String password) {
        LoggerUtil.info(LOGGER, "enter in UserDaoImpl[queryByUserNameAndPassword],username:{0},password:{1}", username, password);
        Map<String,String> map= Maps.newHashMap();
        map.put("username",username);
        map.put("password",password);
        List<UserDO> userDOList = getSqlSession().selectList("songyutong.queryByUserNameAndPassword",map);
        //防止下游空指针
        if (userDOList==null){
            return Lists.newArrayList();
        }
        return userDOList;
    }

    @Override
    public UserDO queryByUserId(long userId) {
        LoggerUtil.info(LOGGER,"enter in UserDaoImpl[queryByUserId],userId:{0}",userId);
        return getSqlSession().selectOne("songyutong.queryByUserId",userId);
    }

    @Override
    public List<UserDO> listAllUsers() {
        LoggerUtil.info(LOGGER,"enter in UserDaoImpl[listAllUsers]");
        return getSqlSession().selectList("songyutong.listAllUsers");
    }

    @Override
    public long insertUser(UserDO userDO) {
        LoggerUtil.info(LOGGER,"enter in UserDaoImpl[insertUser],userDo:{0}",userDO);
        long id=getSqlSession().insert("songyutong.insertUser",userDO);
        return userDO.getUserId();
    }

    @Override
    public long updateUser(UserDO userDO) {
        LoggerUtil.info(LOGGER,"enter in UserDaoImpl[updateUser],userDo:{0}",userDO);
        long id=getSqlSession().update("songyutong.updateUser",userDO);
        return userDO.getUserId();
    }

    @Override
    public long deleteUser(long userId) {
        LoggerUtil.info(LOGGER,"enter in UserDaoImpl[deleteUser],userId{0}",userId);
        return getSqlSession().delete("songyutong.deleteUser",userId);
    }
}
