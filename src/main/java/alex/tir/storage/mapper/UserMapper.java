package alex.tir.storage.mapper;

import alex.tir.storage.dto.UserInfo;
import alex.tir.storage.entity.Role;
import alex.tir.storage.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "dateCreated", target = "dateRegistered")
    UserInfo mapUser(User user);

    List<UserInfo> mapUsers(Iterable<User> users);

    default String roleToString(Role role) {
        return role.getName();
    }
}
