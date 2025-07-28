package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserMapper userMapper;
    final UserRepository userRepository;

    @Override
    public UserDto createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.toUser(createUserRequest);
        UserDto userDto = userMapper.toDto(userRepository.save(user));
        log.info("User with id = {} was created", userDto.getId());
        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getUsers(List<Long> userIds, int from, int size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<User> page;
        if (userIds != null && !userIds.isEmpty()) {
            page = userRepository.findUsersByIdIn(userIds, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        log.info("Get users with {ids, from, size} = ({}, {}, {})", userIds, from, size);
        return page.getContent().stream().map(userMapper::toDto).toList();
    }

    @Override
    public void deleteUserById(Long userId) {
        if (userRepository.deleteUserById(userId).isPresent()) {
            log.info("User with id = {} was deleted", userId);
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Get user with id = {}", userId);
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id = %d not found", userId))));
    }
}