package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Message 읽음 상태 생성 정보")
public class ReadStatusCreateRequestDto {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public static ReadStatusCreateRequestDto from(ReadStatus readStatus){
        return ReadStatusCreateRequestDto.builder()
                .userId(readStatus.getUser().getId())
                .channelId(readStatus.getChannel().getId())
                .build();
    }
}
