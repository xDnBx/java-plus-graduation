package ru.practicum.user.service;

import ru.practicum.user.dto.CreateUserRequest;
import ru.practicum.user.dto.UserResponse;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest createUserRequest);

    void deleteUserById(Long userId);

    Collection<UserResponse> getUsers(List<Long> userIds, int from, int size);
}