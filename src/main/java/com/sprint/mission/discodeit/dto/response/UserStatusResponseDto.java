package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UserStatusResponseDto {
    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;

    public static UserStatusResponseDto from(UserStatus userStatus){
        return UserStatusResponseDto.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUser().getId())
                .lastActiveAt(Instant.now())
                .build();
    }
}
