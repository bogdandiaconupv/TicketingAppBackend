package com.ticketingapp.shared.mappers;

import com.ticketingapp.auth.dto.UserDto;
import com.ticketingapp.auth.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto userToDtoMapper(User user){
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public static List<UserDto> usersToDtoMapper(List<User> users){
        return users.stream().map(UserMapper::userToDtoMapper).collect(Collectors.toList());
    }
}
