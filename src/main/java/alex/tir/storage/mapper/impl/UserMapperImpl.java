package alex.tir.storage.mapper.impl;

import alex.tir.storage.dto.UserInfo;
import alex.tir.storage.entity.Role;
import alex.tir.storage.entity.User;
import alex.tir.storage.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserInfo mapUser(User user) {
        if (user == null){
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setDateModified(user.getDateModified());
        userInfo.setDateRegistered(user.getDateCreated());
        userInfo.setRoles(roleSetToStringSet(user.getRoles()));
        return userInfo;
    }

    protected Set<String> roleSetToStringSet(Set<Role> set) {
        if (set == null) {
            return null;
        }
        Set<String> set1 = new HashSet<>(Math.max((int)(set.size()/.75f) + 1, 16));
        for (Role role : set) {
            set1.add(roleToString(role));
        }
        return set1;
    }

    @Override
    public List<UserInfo> mapUsers(Iterable<User> users) {
        if (users == null) {
            return null;
        }
        List<UserInfo> list = new ArrayList<>();
        for (User user : users) {
            list.add(mapUser(user));
        }
        return list;
    }
}
