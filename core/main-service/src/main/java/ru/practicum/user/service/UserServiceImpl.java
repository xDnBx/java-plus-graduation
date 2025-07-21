package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.CreateUserRequest;
import ru.practicum.user.dto.UserResponse;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserMapper userMapper;
    final UserRepository userRepository;

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.requestToUser(createUserRequest);
        UserResponse userResponse = userMapper.userToResponse(userRepository.save(user));
        log.info("User with id={} was created", userResponse.getId());
        return userResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserResponse> getUsers(List<Long> userIds, int from, int size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<User> page;
        if (userIds != null && !userIds.isEmpty()) {
            page = userRepository.findUsersByIdIn(userIds, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        log.info("Get users with {ids, from, size} = ({}, {}, {})", userIds, from, size);
        return page.getContent().stream().map(userMapper::userToResponse).toList();
    }

    @Override
    public void deleteUserById(Long userId) {
        if (userRepository.deleteUserById(userId).isPresent()) {
            log.info("User with id={} was deleted", userId);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
    }
}