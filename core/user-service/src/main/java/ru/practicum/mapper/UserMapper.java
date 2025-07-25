package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

@Mapper
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(CreateUserRequest createUserRequest);

    UserDto toDto(User user);
}