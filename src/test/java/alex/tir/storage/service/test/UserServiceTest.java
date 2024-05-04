package alex.tir.storage.service.test;

import alex.tir.storage.dto.UserForm;
import alex.tir.storage.dto.UserInfo;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.EmailAlreadyExistsException;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.UserMapper;
import alex.tir.storage.repo.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static alex.tir.storage.util.EntityBuilders.defaultUser;
import static alex.tir.storage.util.FormBuilders.defaultUserForm;
import static alex.tir.storage.util.ProjectionBuilders.defaultUserInfo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserServiceImpl userService;


    @Test
    public void getAllUsers_shouldReturnAllUsersInfo() {
        List<User> users = List.of(
                defaultUser().id(0L).build(),
                defaultUser().id(1L).build());
        List<UserInfo> userInfoList = List.of(
                defaultUserInfo().id(2L).build(),
                defaultUserInfo().id(3L).build());

        when(userRepository.findAllJoinRoles()).thenReturn(users);
        when(userMapper.mapUsers(users)).thenReturn(userInfoList);

        List<UserInfo> returnedUserInfoList = userService.getAllUsers();

        assertThat(returnedUserInfoList, equalTo(userInfoList));
    }

    @Test
    public void getUserInfo_whenUserFound_shouldReturnUserInfo() {
        long userId = 0L;
        User user = defaultUser().id(userId).build();
        UserInfo userInfo = defaultUserInfo().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.mapUser(user)).thenReturn(userInfo);

        UserInfo returnedUserInfo = userService.getUserInfo(userId);

        assertThat(returnedUserInfo, equalTo(userInfo));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getUserInfo_whenUserNotFound_shouldThrowException() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        userService.getUserInfo(userId);
    }

    @Test
    public void updateUserInfo_whenUserFoundAndEmailIsUnique_shouldUpdateUser() {
        long userId = 0L;
        User user = defaultUser().id(userId).build();
        UserForm userForm = defaultUserForm().build();
        String encodedPassword = new StringBuilder(userForm.getPassword()).reverse().toString();
        UserInfo userInfo = defaultUserInfo().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase(userForm.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userForm.getPassword())).thenReturn(encodedPassword);
        when(userMapper.mapUser(user)).thenReturn(userInfo);

        UserInfo returnedUserInfo = userService.updateUserInfo(userId, userForm);

        assertThat(user.getFirstName(), equalTo(userForm.getFirstName()));
        assertThat(user.getLastName(), equalTo(userForm.getLastName()));
        assertThat(user.getEmail(), equalTo(userForm.getEmail()));
        assertThat(user.getPassword(), equalTo(encodedPassword));

        assertThat(returnedUserInfo, equalTo(userInfo));
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateUserInfo_whenUserNotFound_shouldThrowException() {
        long userId = 0L;
        UserForm userForm = defaultUserForm().build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userService.updateUserInfo(userId, userForm);
    }

    @Test(expected = EmailAlreadyExistsException.class)
    public void updateUserInfo_whenEmailIsNotUnique_shouldThrowException() {
        long userId = 0L;
        User user = defaultUser().id(userId).build();
        UserForm userForm = defaultUserForm().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase(userForm.getEmail())).thenReturn(true);

        userService.updateUserInfo(userId, userForm);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteUser_whenUserNotFound_shouldThrowException() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        userService.deleteUser(userId);
    }

}