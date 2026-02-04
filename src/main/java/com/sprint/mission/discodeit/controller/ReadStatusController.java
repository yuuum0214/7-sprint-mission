package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
        return readStatusService.create(readStatusCreateRequestDto);
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatusDto updateStatus(@PathVariable UUID readStatusId,
                                      @RequestBody ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        readStatusUpdateRequestDto.setNewLastReadAt(readStatusUpdateRequestDto.getNewLastReadAt());
        return readStatusService.update(readStatusId, readStatusUpdateRequestDto);
    }

    @GetMapping(params = "userId")
    public List<ReadStatusDto> findAllByUserId /*getReadStatus*/ (@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}
