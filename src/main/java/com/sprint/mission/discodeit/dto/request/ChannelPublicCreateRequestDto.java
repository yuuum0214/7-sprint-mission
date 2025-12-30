package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Public Channel 생성 정보")
public class ChannelPublicCreateRequestDto {

    private String name;
    private String description;
}
