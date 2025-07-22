package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.user.dto.CreateUserRequest;
import ru.practicum.user.dto.UserResponse;
import ru.practicum.model.User;

@Mapper
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User requestToUser(CreateUserRequest createUserRequest);

    UserResponse userToResponse(User user);
}