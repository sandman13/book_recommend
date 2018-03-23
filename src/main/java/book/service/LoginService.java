package book.service;

import book.domain.dto.UserDTO;

/**
 * @author yutong song
 * @date 2018/3/20
 */
public interface LoginService {

    UserDTO hasMatchUser(String username, String password);

}
