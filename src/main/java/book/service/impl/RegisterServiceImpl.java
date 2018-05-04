package book.service.impl;

import book.dao.UserDao;
import book.domain.Enum.StatusEnum;
import book.domain.dataobject.UserDO;
import book.domain.dto.UserDTO;
import book.service.LoginService;
import book.service.RegisterService;
import book.util.DateUtils;
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

    public boolean insertUser(UserDTO userDTO) {
        long id=userDao.insertUser(convertDTOToDO(userDTO));
        return id>0;

    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDO convertDTOToDO(UserDTO userDTO)
    {
        UserDO userDO=new UserDO();
        userDO.setUsername(userDTO.getUsername());
        userDO.setPassword(userDTO.getPassword());
        userDO.setEmail(userDTO.getEmail());
        userDO.setPhoneNumber(userDTO.getPhoneNumber());
        userDO.setSex(userDTO.getSex());
        userDO.setAge(userDTO.getAge());
        userDO.setProfession(userDTO.getProfession());
        userDO.setUserStatus(StatusEnum.READER.name());
        return userDO;
    }
}
