package book.service.impl;

import book.dao.UserDao;
import book.domain.dataobject.UserDO;
import book.domain.dto.UserDTO;
import book.service.LoginService;
import book.util.DateUtils;
import book.util.LoggerUtil;
import book.util.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yutong song
 * @date 2018/3/20
 */
@Service(value = "LoginService")
public class LoginServiceImpl implements LoginService {

    @Resource(name = "userDao")
    private UserDao userDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    @Override
    public UserDTO hasMatchUser(String username, String password) {
        LoggerUtil.info(LOGGER, "enter in LoginServiceImpl[hasMatchUser],username:{0},password:{1}", username, password);
        List<UserDO> userDOList = userDao.queryByUserNameAndPassword(username, password);
        ValidateUtils.checkNotEmpty(userDOList, "用户名或密码不存在");
        //如果有多个则使用第一个,确保用户名唯一
        ValidateUtils.checkNotEmpty(userDOList.get(0).getUserStatus(), "用户名和密码不存在");
        return convertDOtoDTO(userDOList.get(0));
    }

    /**
     * 用户DO对象转DTOd对象
     *
     * @param userDO
     * @return
     */
    private UserDTO convertDOtoDTO(UserDO userDO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userDO.getUserId());
        userDTO.setUsername(userDO.getUsername());
        userDTO.setUserStatus(userDO.getUserStatus());
        userDTO.setGmtModified(DateUtils.format(userDO.getGmtCreate()));
        userDTO.setGmtModified(DateUtils.format(userDO.getGmtModified()));
        return userDTO;
    }
}
