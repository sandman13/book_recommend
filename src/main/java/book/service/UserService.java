package book.service;

import book.domain.dto.UserDTO;

import java.util.List;

public interface UserService {
    boolean updateUser(UserDTO userDTO);

    List<UserDTO> listAllUsers();

    boolean deleteUser(long UserId);

    List<UserDTO> queryByName(String username);
}
