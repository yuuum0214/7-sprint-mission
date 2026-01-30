package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {

    //의존성 주입
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;

    @Transactional
    @Override
    public MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDto,
                                            List<MultipartFile> files) {
        Channel channel = channelRepository.findById(messageCreateRequestDto.getChannelId())
                .orElseThrow(() -> new IllegalStateException("채널정보를 찾을 수 없습니다."));

        User user = userRepository.findById(messageCreateRequestDto.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("작성자가 없습니다."));

        List<BinaryContent> attachments = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                try {
                    BinaryContent binaryContent = new BinaryContent(
                            file.getOriginalFilename(),
                            file.getSize(),
                            file.getContentType()
                    );
                    BinaryContent saved = binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(saved.getId(), file.getBytes());
                    attachments.add(saved);
                } catch (IOException e){
                    log.error("파일 저장 실패", e);
                    throw new RuntimeException("파일 저장 실패", e);
                }
            }
        }

        Message message = new Message(
                channel,
                user,
                messageCreateRequestDto.getContent(),
                attachments
        );
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageResponseDto findByMessage(UUID uuid) {
        Message message = messageRepository.findById(uuid).orElse(null);

        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponseDto> findUserAllMessage(UUID userId) {
        if (userId == null) {
            throw new IllegalStateException("유저 정보가 없습니다.");
        }

        List<Message> messages = messageRepository.findAll().stream()
                .filter(m -> m.getId().equals(userId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
        return messageMapper.toDtoList(messages);
    }

    @Transactional(readOnly = true)
    @Override
    public Slice<MessageResponseDto> findChannelAllMessage(UUID channelId, Pageable pageable) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 정보가 없습니다.");
        }
        Slice<Message> messages = messageRepository.findAllByChannelId(channelId, pageable);

        return messages.map(messageMapper::toDto);
    }

    @Transactional
    @Override
    public MessageResponseDto updateMessage(UUID messageId, MessageUpdateRequestDto messageUpdateRequestDto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다."));

        message.setUpdate(messageUpdateRequestDto.getNewContent());

//        List<BinaryContent> attachments = message.getAttachments() != null
//                ? new ArrayList<>(message.getAttachments())
//                : new ArrayList<>();
//        message.setAttachmentIds(attachments);
//        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void deleteMessage(UUID uuid) {
        Message message = messageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다."));

        List<BinaryContent> attachments = message.getAttachments();
        if (attachments != null) {
            for (BinaryContent attachmentId : attachments) {
                binaryContentRepository.delete(attachmentId);
            }
        }

        messageRepository.delete(message);
        System.out.println("[Message 삭제] : " + messageRepository.findById(uuid));
    }
}
