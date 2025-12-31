package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    @Override
    public Message createMessage(MessageCreateRequestDto messageCreateRequestDto,
                                 List<MultipartFile> files) {
        Channel channel = channelRepository.findById(messageCreateRequestDto.getChannelId())
                .orElseThrow(() -> new IllegalStateException("채널정보를 찾을 수 없습니다."));

        User user = userRepository.findById(messageCreateRequestDto.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("작성자가 없습니다."));

        List<BinaryContent> attachments = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;
                BinaryContent saved = binaryContentRepository.save(
                        new BinaryContent(file.getOriginalFilename(), file.getSize(), file.getContentType()));
                attachments.add(saved);
            }
        }

        Message message = new Message(
                channel,
                user,
                messageCreateRequestDto.getContent(),
                attachments
        );
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    @Override
    public Message findByMessage(UUID uuid) {
        return messageRepository.findById(uuid)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Message> findUserAllMessage(UUID userId) {
        if (userId == null) {
            throw new IllegalStateException("유저 정보가 없습니다.");
        }

        return messageRepository.findAll().stream()
                .filter(m -> m.getId().equals(userId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Message> findChannelAllMessage(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 정보가 없습니다.");
        }
        return messageRepository.findAllByChannelId(channelId);
    }

    @Transactional
    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequestDto messageUpdateRequestDto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다."));

        message.setUpdate(messageUpdateRequestDto.getNewContent());

        List<BinaryContent> attachments = message.getAttachments() != null
                ? new ArrayList<>(message.getAttachments())
                : new ArrayList<>();
        message.setAttachmentIds(attachments);
        System.out.println("[Message 수정] : " + message.getContent());
        return messageRepository.save(message);
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
