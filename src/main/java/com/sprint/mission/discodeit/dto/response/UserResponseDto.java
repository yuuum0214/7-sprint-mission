package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private BinaryContentResponseDto profile;
    private Boolean online;

    public static UserResponseDto from(User user, UserStatus userStatus, BinaryContent binaryContent) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUserName())
                .email(user.getEmail())
                .online(userStatus != null && userStatus.isOnline())
                .profile(binaryContent != null
                        ? BinaryContentResponseDto.from(binaryContent) : null)
                .build();
    }
}
