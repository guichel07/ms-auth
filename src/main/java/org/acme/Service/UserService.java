package org.acme.Service;

import java.util.List;

import org.acme.DTO.EmailLoginRequestDTO;
import org.acme.DTO.UserAdminUpdateDTO;
import org.acme.DTO.UserCreateDTO;
import org.acme.DTO.UserCredentialsDTO;
import org.acme.DTO.UserDTO;
import org.acme.DTO.UserUpdateDTO;

public interface UserService {
    UserDTO loginWithEmail(EmailLoginRequestDTO emailLoginRequestDTO);
    UserDTO loginWithEmailByAdmin(EmailLoginRequestDTO emailLoginRequestDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsersDTO();
    void deleteUserId(Long id);
    UserCredentialsDTO registerUser(UserCreateDTO userCreateDTO);
    List<UserCredentialsDTO> registerAllUsers(List<UserCreateDTO> usersCreateDTO);
    UserDTO updateUser(UserUpdateDTO userUpdateDTO, String emailFromToken);
    UserDTO adminUpdateUser(Long id, UserAdminUpdateDTO userAdminUpdateDTO);
    UserCredentialsDTO updatePassword(String email);
}
