package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Private Channel 생성 정보")
public class ChannelPrivateCreateRequestDto {

    private List<UUID> participantIds; // PRIVATE 일 때 사용

    public static ChannelPrivateCreateRequestDto from(ChannelPrivateCreateRequestDto dto) {
        return ChannelPrivateCreateRequestDto.builder()
                .participantIds(dto.getParticipantIds())
                .build();
    }
}
