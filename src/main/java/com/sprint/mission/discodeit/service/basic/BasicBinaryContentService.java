package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentResponseDto create(BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        BinaryContent binaryContent = new BinaryContent(
                binaryContentCreateRequestDto.getFileName(),
                (long) binaryContentCreateRequestDto.getBytes().length,
                binaryContentCreateRequestDto.getContentType()
        );
        BinaryContent saved = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(saved.getId(), binaryContentCreateRequestDto.getBytes());

        return binaryContentMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentResponseDto find(UUID uuid) {
        BinaryContent binaryContent = binaryContentRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("BinaryContent를 찾을 수 없습니다."));
        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentResponseDto> findByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("User not found"));
        UUID profileId = user.getProfile().getId();
        if(profileId==null){
            return List.of();
        }
        BinaryContent binaryContent = user.getProfile();

        return List.of(binaryContentMapper.toDto(binaryContent));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentResponseDto> findAllByIds(List<UUID> uuids) {
        return binaryContentRepository.findAllById(uuids).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentResponseDto> findByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);

        List<UUID> attachmentIds = messages.stream()
                .flatMap(message->message.getAttachments().stream())
                .map(BinaryContent::getId)
                .toList();

        if(attachmentIds.isEmpty()){
            return List.of();
        }

        return attachmentIds.stream()
                .map(binaryContentRepository::findById)
                .flatMap(Optional::stream)
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        BinaryContent binaryContent = binaryContentRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("BinaryContent를 찾을 수 없습니다."));
        binaryContentRepository.delete(binaryContent);
    }
}
