package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.feign.UserClient;
import ru.practicum.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUserController implements UserClient {
    final UserService userService;

    @Override
    public UserDto createUser(CreateUserRequest createUserRequest) {
        log.info("Request to create user = {}", createUserRequest);
        return userService.createUser(createUserRequest);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Request to delete user with id = {}", userId);
        userService.deleteUserById(userId);
    }

    @Override
    public Collection<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("Request to get users with ids = {}, from = {}, size = {}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Request to get user with id = {}", userId);
        return userService.getUserById(userId);
    }
}