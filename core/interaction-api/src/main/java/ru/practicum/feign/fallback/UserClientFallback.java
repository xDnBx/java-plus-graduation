package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ServiceUnavailableException;
import ru.practicum.feign.UserClient;

import java.util.Collection;
import java.util.List;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserDto createUser(CreateUserRequest createUserRequest) {
        throw new ServiceUnavailableException("Client-Service is unavailable");
    }

    @Override
    public void deleteUser(Long userId) {
        throw new ServiceUnavailableException("Client-Service is unavailable");
    }

    @Override
    public Collection<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        throw new ServiceUnavailableException("Client-Service is unavailable");
    }

    @Override
    public UserDto getUserById(Long userId) {
        throw new ServiceUnavailableException("Client-Service is unavailable");
    }
}