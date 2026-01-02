package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserStatusMapper {
    public UserStatusResponseDto toDto(UserStatus userStatus) {
        if(userStatus == null){
            return null;
        }

        return UserStatusResponseDto.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUser() != null ? userStatus.getUser().getId() : null)
                .lastActiveAt(userStatus.getLastActiveAt())
                .build();
    }

    public List<UserStatusResponseDto> toDtoList(List<UserStatus> userStatuses) {
        if(userStatuses == null || userStatuses.isEmpty()){
            return List.of();
        }
        return userStatuses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
