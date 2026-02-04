package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.PageResponseDto;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequestDto messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> files) {

        return messageService.createMessage(messageCreateRequest, files);
    }

    // 메시지 수정
    @PatchMapping(value = "/{messageId}")
    public MessageResponseDto updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequestDto messageUpdateRequest) {
        return messageService.updateMessage(messageId, messageUpdateRequest);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널 메시지 목록 조회
    @GetMapping
    public PageResponseDto<MessageResponseDto> getMessageByChannel(
            @RequestParam UUID channelId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Slice<MessageResponseDto> messages = messageService.findChannelAllMessage(channelId, pageable);
        return PageResponseMapper.fromSlice(messages);
    }
}
