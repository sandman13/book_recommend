package book.service;

import book.domain.dto.UserDTO;

public interface RegisterService {
    boolean insertUser(UserDTO userDTO);
}
