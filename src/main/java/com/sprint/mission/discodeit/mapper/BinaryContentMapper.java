package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BinaryContentMapper {

    public BinaryContentResponseDto toDto(BinaryContent binaryContent){
        if(binaryContent==null){
            return null;
        }
        return BinaryContentResponseDto.builder()
                .id(binaryContent.getId())
                .fileName(binaryContent.getFileName())
                .size(binaryContent.getSize())
                .contentType(binaryContent.getContentType())
                .build();
    }

    public List<BinaryContentResponseDto> toDtoList(List<BinaryContent> binaryContents){
        if(binaryContents==null || binaryContents.isEmpty()){
            return List.of();
        }
        return binaryContents.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
