package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateRequestDto binaryContentCreateRequestDto);

    BinaryContentResponseDto find(UUID uuid);

    List<BinaryContentResponseDto> findByUserId(UUID userId);

    List<BinaryContentResponseDto> findByChannelId(UUID channelId);

    void delete(UUID uuid);

    List<BinaryContentResponseDto> findAllByIds(List<UUID> uuids);
}
