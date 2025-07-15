package ru.practicum.explore_with_me.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.user.dto.CreateUserRequest;
import ru.practicum.explore_with_me.user.dto.UserResponse;
import ru.practicum.explore_with_me.user.model.User;

@Mapper
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User requestToUser(CreateUserRequest createUserRequest);

    UserResponse userToResponse(User user);
}
