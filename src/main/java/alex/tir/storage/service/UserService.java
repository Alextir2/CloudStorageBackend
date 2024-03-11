package alex.tir.storage.service;

import alex.tir.storage.dto.UserForm;
import alex.tir.storage.dto.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> getAllUsers();

    UserInfo getUserInfo(Long userId);

    UserInfo updateUserInfo(Long id, UserForm userForm);

    void deleteUser(Long userId);
}
