package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveFailException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
                .orElseThrow(() -> new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND));

        User user = userRepository.findById(messageCreateRequestDto.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

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
                    throw new BinaryContentSaveFailException(ErrorCode.BINARY_CONTENT_SAVE_FAILED);
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

        log.info("Created Message Username : {}", message.getAuthor().getUsername());
        log.info("Created Message ChannelType: {} | ChannelName: {}", message.getChannel().getType(), message.getChannel().getName());
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
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        List<Message> messages = messageRepository.findAll().stream()
                .filter(m -> m.getAuthor().getId().equals(userId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
        return messageMapper.toDtoList(messages);
    }

    @Transactional(readOnly = true)
    @Override
    public Slice<MessageResponseDto> findChannelAllMessage(UUID channelId, Pageable pageable) {
        if (channelId == null) {
            throw new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND);
        }
        Slice<Message> messages = messageRepository.findAllByChannelId(channelId, pageable);

        return messages.map(messageMapper::toDto);
    }

    @Transactional
    @Override
    public MessageResponseDto updateMessage(UUID messageId, MessageUpdateRequestDto messageUpdateRequestDto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND));

        message.setUpdate(messageUpdateRequestDto.getNewContent());

        log.info("Updated Message Username: {}", message.getAuthor().getUsername());
        log.info("Updated Message ChannelType: {} | ChannelName: {}", message.getChannel().getType(), message.getChannel().getName());
        log.info("Updated Message Content: {}", message.getContent());
        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void deleteMessage(UUID uuid) {
        Message message = messageRepository.findById(uuid)
                .orElseThrow(() -> new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND));

        List<BinaryContent> attachments = message.getAttachments();
        if (attachments != null) {
            for (BinaryContent attachmentId : attachments) {
                binaryContentRepository.delete(attachmentId);
            }
        }

        messageRepository.delete(message);
        log.info("Deleted Message Username: {}", message.getAuthor().getUsername());
        log.info("Deleted Message ChannelType: {} | ChannelName : {}", message.getChannel().getType(), message.getChannel().getName());
    }
}
