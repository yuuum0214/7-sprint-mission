package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus")
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ReadStatusDto create_1(@RequestBody ReadStatusCreateRequestDto readStatusCreateRequestDto) {
//        readStatusCreateRequestDto.getUserId();
//        readStatusCreateRequestDto.getChannelId();
        return readStatusService.create(readStatusCreateRequestDto);
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatusDto updateStatus(@Parameter(description = "수정할 읽음 상태 ID")
                                      @PathVariable UUID readStatusId,
                                      @RequestBody ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
//        readStatusUpdateRequestDto.setId(readStatusId);
        readStatusUpdateRequestDto.setNewLastReadAt(readStatusUpdateRequestDto.getNewLastReadAt());
        return readStatusService.update(readStatusId, readStatusUpdateRequestDto);
    }

    @GetMapping(params = "userId")
    public List<ReadStatusDto> findAllByUserId /*getReadStatus*/(@Parameter(description = "조회할 User ID")
                                                                 @RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}
