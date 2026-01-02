package com.sprint.mission.discodeit.dto.response;

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
}
