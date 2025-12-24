package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional
    @Override
    public BinaryContentResponseDto create(BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        BinaryContent entity = new BinaryContent(
                binaryContentCreateRequestDto.getFileName(),
                (long) binaryContentCreateRequestDto.getBytes().length,
                binaryContentCreateRequestDto.getContentType()
        );
        binaryContentRepository.save(entity);
        return BinaryContentResponseDto.from(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentResponseDto find(UUID uuid) {
        BinaryContent entity = binaryContentRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("BinaryContent를 찾을 수 없습니다."));
        if (entity == null) {
            throw new RuntimeException("Binary content를 찾을 수 없음");
        }
        return BinaryContentResponseDto.from(entity);
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
        BinaryContent content = binaryContentRepository.findById(profileId)
                .orElseThrow(()->new IllegalArgumentException("BinaryContent를 찾을 수 없습니다."));
        if(content==null){
            return List.of();
        }

        return List.of(BinaryContentResponseDto.from(content));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentResponseDto> findAllByIds(List<UUID> uuids) {
        return binaryContentRepository.findAllById(uuids).stream()
                .map(BinaryContentResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentResponseDto> findByChannelId(Channel channelId) {
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
                .map(BinaryContentResponseDto::from)
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
