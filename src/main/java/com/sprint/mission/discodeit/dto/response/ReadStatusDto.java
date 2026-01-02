package com.sprint.mission.discodeit.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ReadStatusDto {
    private UUID id; //uuid; // DTO의 ID
    private UUID userId; // 읽은 유저 식별
    private UUID channelId; // 읽음 상태 적용 채널
    private Instant lastReadAt; // 마지막 메시지 읽은 시각
}
