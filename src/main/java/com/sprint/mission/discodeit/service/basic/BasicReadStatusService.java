package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        ReadStatus existing = readStatusRepository.findByUserIdAndChannelId(
                readStatusCreateRequestDto.getUser().getId(),
                readStatusCreateRequestDto.getChannel().getId()
        );
        if (existing != null) throw new IllegalArgumentException("ReadStatus already exists");

        User user = userRepository.findById(readStatusCreateRequestDto.getUser().getId())
                .orElseThrow(()->new IllegalArgumentException("User not found"));

        Channel channel = channelRepository.findById(readStatusCreateRequestDto.getChannel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatusRepository.save(readStatus);

        return ReadStatusDto.from(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto findById(UUID uuid) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("ReadStatus를 찾을 수 없습니다."));
        if (readStatus == null) throw new IllegalArgumentException("ReadStatus not found");
        return ReadStatusDto.from(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("User not found"));
        return readStatusRepository.findByUserId(user.getId())
                .stream()
                .map(ReadStatusDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ReadStatusDto update(ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusUpdateRequestDto.getId())
                .orElseThrow(()->new IllegalArgumentException("ReadStatus not found"));

        if (readStatusUpdateRequestDto.getNewLastReadAt() != null
                && readStatusUpdateRequestDto.getNewLastReadAt().isAfter(readStatus.getLastReadAt())) {

            readStatus.setUpdate(readStatusUpdateRequestDto.getNewLastReadAt());
        }
        return ReadStatusDto.from(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("ReadStatus not found"));
        if(readStatus == null) throw new IllegalArgumentException("ReadStatus not found");
        readStatusRepository.delete(readStatus);
    }
}
