package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReadStatusMapper {
    public ReadStatusDto toDto(ReadStatus readStatus){
        if(readStatus == null){
            return null;
        }
        return ReadStatusDto.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUser() != null ? readStatus.getUser().getId() : null)
                .channelId(readStatus.getChannel() != null ? readStatus.getChannel().getId() : null)
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }

    public List<ReadStatusDto> toDtoList(List<ReadStatus> readStatuses){
        if(readStatuses == null || readStatuses.isEmpty()){
            return List.of();
        }
        return readStatuses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
