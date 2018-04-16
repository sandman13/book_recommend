package book.service.impl;

import book.dao.UserDao;
import book.domain.dataobject.UserDO;
import book.domain.dto.UserDTO;
import book.service.UserService;
import book.util.DateUtils;
import book.util.LoggerUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author hui zhang
 * @date 2018-4-15
 */
@Service(value = "UserService")
public class UserServiceImpl implements UserService {

   @Resource(name = "userDao")
    private  UserDao userDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Override
    public boolean updateUser(UserDTO userDTO) {
        LoggerUtil.info(LOGGER,"enter in UserServiceImpl[updateUser],userDTO:{0}",userDTO);
        long id=userDao.updateUser(convertToDO(userDTO));
        return id>0;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        LoggerUtil.info(LOGGER,"enter in UserServiceImpl[listAllUsers]");
        List<UserDO> userDOList=userDao.listAllUsers();
        return convertDOSToDTOS(userDOList);
    }

    @Override
    public boolean deleteUser(long UserId) {
        LoggerUtil.info(LOGGER,"enter in UserServiceImpl[deleteUser],UserId:{0}",UserId);
        long id=userDao.deleteUser(UserId);
        return id>0;
    }

    /**
     * userDTO对象转为userDO对象
     * @param userDTO
     * @return
     */
    private UserDO convertToDO(UserDTO userDTO)
    {
        UserDO userDO=new UserDO();
        userDO.setUserId(userDTO.getUserId());
        userDO.setUsername(userDTO.getUsername());
        userDO.setPassword(userDTO.getPassword());
        userDO.setEmail(userDTO.getEmail());
        userDO.setModifier(userDTO.getModifier());
        userDO.setPhoneNumber(userDTO.getPhoneNumber());
        return userDO;
    }

    /**
     * UserDO对象转为UserDTO对象
     * @param userDO
     * @return
     */
    private UserDTO convertToDTO(UserDO userDO)
    {
        UserDTO userDTO=new UserDTO();
        userDTO.setUserId(userDO.getUserId());
        userDTO.setUsername(userDO.getUsername());
        userDTO.setPassword(userDO.getPassword());
        userDTO.setEmail(userDO.getEmail());
        userDTO.setGmtCreate(DateUtils.format(userDO.getGmtCreate()));
        userDTO.setGmtModified(DateUtils.format(userDO.getGmtModified()));
        userDTO.setModifier(userDO.getModifier());
        userDTO.setPhoneNumber(userDO.getPhoneNumber());
        return userDTO;
    }

    /**
     * UserDTO列表转为UserDO列表
     * @param userDOList
     * @return
     */
    private List<UserDTO> convertDOSToDTOS(List<UserDO> userDOList)
    {
        List<UserDTO> userDTOList= Lists.newArrayList();
        if (!CollectionUtils.isEmpty(userDOList)) {
            for (UserDO userDO : userDOList) {
                UserDTO userDTO = convertToDTO(userDO);
                userDTOList.add(userDTO);
            }
        }
        return userDTOList;
    }
}
