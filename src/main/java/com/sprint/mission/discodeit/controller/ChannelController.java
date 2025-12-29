package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.request.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel")
@Slf4j
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;

    // 공개 채널 생성
    @PostMapping("/public")
    public ResponseEntity<Void> createPublicChannel(@RequestBody ChannelPublicCreateRequestDto channelPublicCreateRequestDto) {
        channelService.createPublicChannel(channelPublicCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 비공개 채널 생성
    @PostMapping("/private")
    public ResponseEntity<Void> createPrivateChannel(@RequestBody ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto) {
        channelService.createPrivateChannel(channelPrivateCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 공개 채널 정보 수정
    @PatchMapping("/{channelId}")
    public void updateChannel(
            @Parameter(description = "수정할 Channel ID")
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequestDto channelUpdateRequestDto) {
        channelService.updateChannel(channelId, channelUpdateRequestDto);
    }

    // 채널 삭제
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @Parameter(description = "삭제할 Channel ID")
            @PathVariable("channelId") UUID uuid) {
        channelService.deleteChannel(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 사용자의 채널 목록 조회
    @GetMapping
    public List<ChannelResponseDto> getChannel(
            @Parameter(description = "조회할 User ID")
            @RequestParam UUID userId) {
        return channelService.findAllByUserId(userId);
    }
}
