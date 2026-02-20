package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "수정할 Channel 정보")
public class ChannelUpdateRequestDto {
    private String newName;
    private String newDescription;
}
