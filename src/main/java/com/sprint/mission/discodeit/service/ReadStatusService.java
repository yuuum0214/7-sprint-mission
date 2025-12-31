package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(ReadStatusCreateRequestDto readStatusCreateRequestDto);

    ReadStatusDto findById(UUID uuid);

    List<ReadStatusDto> findAllByUserId(UUID uuid);

    ReadStatusDto update(UUID id, ReadStatusUpdateRequestDto readStatusUpdateRequestDto);

    void delete (UUID uuid);
}
