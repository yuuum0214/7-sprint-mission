package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;

    public UserMapper(BinaryContentMapper binaryContentMapper) {
        this.binaryContentMapper = binaryContentMapper;
    }

    public UserResponseDto toDto(User user){
        if(user==null){
            return null;
        }
        boolean isOnline = user.getUserStatus() != null && user.getUserStatus().isOnline();

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(binaryContentMapper.toDto(user.getProfile()))
                .online(isOnline)
                .build();
    }

    public List<UserResponseDto> toDtoList(List<User> users){
        if(users == null || users.isEmpty()){
            return List.of();
        }

        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
