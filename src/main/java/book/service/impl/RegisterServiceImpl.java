package book.service.impl;

import book.dao.UserDao;
import book.domain.Enum.StatusEnum;
import book.domain.dataobject.UserDO;
import book.service.LoginService;
import book.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hui zhang
 * @date 2018-4-1
 */
@Service(value="RegisterService")
public class RegisterServiceImpl implements RegisterService {
    @Resource(name = "userDao")
    private UserDao userDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    public boolean insertUser(String username, String password) {
        long id=userDao.insertUser(convertToDO(username,password));
        return id>0;

    }
    private UserDO convertToDO(String username, String password) {
        UserDO userDO=new UserDO();
        userDO.setUsername(username);
        userDO.setPassword(password);
        userDO.setUserStatus(StatusEnum.READER.name());
        return userDO;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
