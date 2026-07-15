package org.acme.Service;

import java.util.List;

import org.acme.DTO.EmailLoginRequestDTO;
import org.acme.DTO.UserCredentialsDTO;
import org.acme.DTO.UserDTO;

public interface UserService {
    UserDTO loginWithEmail(EmailLoginRequestDTO emailLoginRequestDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsersDTO();
    void deleteUserId(Long id);
    UserCredentialsDTO registerUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO, String emailFromToken);
    UserCredentialsDTO updatePassword(String email);
}
