package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class BinaryContentResponseDto {

    private UUID id;
    private String fileName;
    private long size;
    private String contentType;

    public static BinaryContentResponseDto from(BinaryContent binaryContent) {
        return BinaryContentResponseDto.builder()
                .id(binaryContent.getId())
                .fileName(binaryContent.getFileName())
                .size(binaryContent.getSize())
                .contentType(binaryContent.getContentType())
                .build();
    }
}
