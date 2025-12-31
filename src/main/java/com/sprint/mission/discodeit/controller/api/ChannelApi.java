package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.ChannelUpdateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface ChannelApi {

    // 공개 채널 생성
    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Public Channel이 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ChannelUpdateResponseDto.class)
                    )
            )
    })
    public ResponseEntity<ChannelResponseDto> createPublicChannel(
            @RequestBody ChannelPublicCreateRequestDto channelPublicCreateRequestDto
    );

    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Private Channel이 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ChannelUpdateResponseDto.class)
                    )
            )
    })
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(
            @RequestBody ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto
    );

    // 공개 채널 정보 수정
    @Operation(summary = "Channel 정보 수정", operationId = "update_3")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ChannelUpdateResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Private Channel은 수정할 수 없음",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject("Private channel cannot be updated"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject("Channel with id {channelId} not found"))
            )
    })
    public void updateChannel(
            @Parameter(description = "수정할 Channel ID")
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequestDto channelUpdateRequestDto
    );

    // 채널 삭제
    @Operation(summary = "Channel 삭제", operationId = "delete_2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel with id {channelId} not found")
                    )
            )
    })
    public ResponseEntity<Void> deleteChannel(
            @Parameter(description = "삭제할 Channel ID")
            @PathVariable("channelId") UUID uuid
    );

    // 특정 사용자의 채널 목록 조회
    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 목록 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ChannelResponseDto.class)
                            )
                    )
            )
    })
    public List<ChannelResponseDto> getChannel(
            @Parameter(description = "조회할 User ID")
            @RequestParam UUID userId
    );
}
