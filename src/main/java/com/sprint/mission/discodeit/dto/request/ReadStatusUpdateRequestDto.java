package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "수정할 읽음 상태 정보")
public class ReadStatusUpdateRequestDto {
    private Instant newLastReadAt; //lastReadAt;


    public void setNewLastReadAt(Instant newLastReadAt) {
        this.newLastReadAt = Instant.now();
    }
}
