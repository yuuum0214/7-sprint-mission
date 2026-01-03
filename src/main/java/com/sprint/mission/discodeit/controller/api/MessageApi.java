package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageApi {

    // 메시지 전송(저장)
    @Operation(summary = "Message 생성", operationId = "create_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel | Author with id {channelId | authorId} not found")
                    )
            )
    })
    public MessageResponseDto createMessage(
            @Parameter(description = "Message 생성 정보")
            @RequestPart("messageCreateRequest") MessageCreateRequestDto messageCreateRequest,
            @RequestParam(value = "attachments", required = false)
            @Parameter(description = "Message 첨부 파일들") List<MultipartFile> files
//            @RequestPart(value = "attachments", required = false) List<MultipartFile> files
    );

    // 메시지 수정
    @Operation(summary = "Message 내용 수정", operationId = "update_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message가 성공적으로 수정됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Message with id {messageId} not found")
                    )
            )
    })
    public MessageResponseDto updateMessage(
            @Parameter(description = "수정할 Message ID")
            @PathVariable UUID messageId
//            @RequestParam String newContent
//            @RequestPart(value = "file", required = false) List<MultipartFile> files
    );

    // 메시지 삭제
    @Operation(summary = "Message 삭제", operationId = "delete_1")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Message with id {messageId} not found")
                    )
            )
    })
    public void deleteMessage(
            @Parameter(description = "삭제할 Message ID")
            @PathVariable UUID messageId
    );

    // 특정 채널 메시지 목록 조회
    @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 목록 조회 성공"
            )
    })
    public List<MessageResponseDto> getMessageByChannel(
            @Parameter(description = "조회할 Channel ID")
            @RequestParam UUID channelId
    );

}
