package book.service;

import book.domain.dto.UserDTO;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public interface LoginService {

    UserDTO hasMatchUser(String username, String password);

}
