package alex.tir.storage.controller;

import alex.tir.storage.dto.UserForm;
import alex.tir.storage.dto.UserInfo;
import alex.tir.storage.entity.UserPrincipal;
import alex.tir.storage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Пользователи")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Получение всех пользователей")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserInfo> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Получение информации о пользователе по id")
    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo getUserInfo(@PathVariable("userId") Long userId) {
        return userService.getUserInfo(userId);
    }
    @Operation(summary = "Получение информации о текущем пользователе")
    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo getItemInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getUserInfo(userPrincipal.getId());
    }

    @Operation(summary = "Обновление информации о текущем пользователе")
    @PostMapping(path = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo updateUserInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserForm userForm) {
        return userService.updateUserInfo(userPrincipal.getId(), userForm);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}
