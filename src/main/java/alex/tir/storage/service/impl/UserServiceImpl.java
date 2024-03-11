package alex.tir.storage.service.impl;

import alex.tir.storage.dto.UserForm;
import alex.tir.storage.dto.UserInfo;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.EmailAlreadyExistsException;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.UserMapper;
import alex.tir.storage.repo.UserRepository;
import alex.tir.storage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional(readOnly = true)
    public List<UserInfo> getAllUsers() {
        List<User> users = userRepository.findAllJoinRoles();
        return userMapper.mapUsers(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(Long userId) {
        User user = getUser(userId);
        return userMapper.mapUser(user);
    }

    @Override
    @Transactional
    public UserInfo updateUserInfo(Long userId, UserForm userForm) {
        User user = getUser(userId);
        if (userForm.getEmail() != null) {
            assertEmailIsUnique(userForm.getEmail());
            user.setEmail(userForm.getEmail());
        }
        if (userForm.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userForm.getPassword());
            user.setPassword(encodedPassword);
        }
        if (userForm.getFirstName() != null) {
            user.setFirstName(userForm.getFirstName());
        }
        if (userForm.getLastName() != null) {
            user.setLastName(userForm.getLastName());
        }
        return userMapper.mapUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new RecordNotFoundException(User.class, userId));
    }
    private void assertEmailIsUnique(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException("The email " + email + " is already in use");
        }
    }
}
