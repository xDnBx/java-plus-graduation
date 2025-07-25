package ru.practicum.service;


import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserRequest createUserRequest);

    void deleteUserById(Long userId);

    Collection<UserDto> getUsers(List<Long> userIds, int from, int size);

    UserDto getUserById(Long userId);
}