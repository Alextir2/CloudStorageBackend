package alex.tir.storage.mapper;

import alex.tir.storage.dto.UserInfo;
import alex.tir.storage.entity.Role;
import alex.tir.storage.entity.User;

import java.util.List;


public interface UserMapper {

    UserInfo mapUser(User user);

    List<UserInfo> mapUsers(Iterable<User> users);
    default String roleToString(Role role) {
        return role.getName();
    }
}
