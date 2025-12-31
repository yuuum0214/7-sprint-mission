package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface ReadStatusApi {

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("ReadStatus with userId {userId} and channelId {channelId} already exists")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel | User with id {channelId | userId} not found")
                    )
            )
    })
    public ReadStatusDto create_1(@RequestBody ReadStatusCreateRequestDto readStatusCreateRequestDto,
                                  @RequestParam UUID userId,
                                  @RequestParam UUID channelId);

    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("ReadStatus with id {readStatusId} not found")
                    )
            )
    })
    public ReadStatusDto updateStatus(@Parameter(description = "수정할 읽음 상태 ID")
                                      @PathVariable UUID readStatusId,
                                      @RequestBody ReadStatusUpdateRequestDto readStatusUpdateRequestDto);

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    })
    public List<ReadStatusDto> findAllByUserId /*getReadStatus*/(@Parameter(description = "조회할 User ID")
                                                                 @RequestParam UUID userId);
}
