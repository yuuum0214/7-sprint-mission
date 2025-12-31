package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.time.Instant;

@Getter
public class UserStatusUpdateRequestDto {
    private Instant lastActiveAt;
}
