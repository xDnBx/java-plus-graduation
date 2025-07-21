package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.user.dto.CreateUserRequest;
import ru.practicum.user.dto.UserResponse;
import ru.practicum.user.model.User;

@Mapper
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User requestToUser(CreateUserRequest createUserRequest);

    UserResponse userToResponse(User user);
}