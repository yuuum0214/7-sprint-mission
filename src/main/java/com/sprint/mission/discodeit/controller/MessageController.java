package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message")
@Slf4j
public class MessageController implements MessageApi {
    private final MessageService messageService;
    private final ChannelRepository channelRepository;

    // 메시지 전송(저장)
    @PostMapping(consumes = "multipart/form-data")
    public MessageResponseDto createMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequestDto messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> files) {
        System.out.println("files: " + files);
        return messageService.createMessage(messageCreateRequest, files);
    }

    // 메시지 수정
    @PatchMapping(value = "/{messageId}") //, consumes = "multipart/form-data")
    public MessageResponseDto updateMessage(
            @PathVariable UUID messageId
//            @RequestParam String newContent
            /*@RequestPart(value = "file", required = false) List<MultipartFile> files*/) {
        MessageUpdateRequestDto dto = new MessageUpdateRequestDto();
//        dto.setMessageId(messageId);
//        dto.setContent(newContent);
        return messageService.updateMessage(messageId, dto); //, files);
//        return MessageResponseDto.from(updateMessage);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public void deleteMessage(
            @PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
    }

    // 특정 채널 메시지 목록 조회
    @GetMapping
    public List<MessageResponseDto> getMessageByChannel(
            @RequestParam UUID channelId) {
        var channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        return messageService.findChannelAllMessage(channelId);
    }
}
