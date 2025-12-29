package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent")
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentResponseDto> findById(
            @Parameter(description = "조회할 첨부 파일 ID")
            @PathVariable("binaryContentId") UUID uuid) {
        return ResponseEntity.ok().body(binaryContentService.find(uuid));
    }

    @GetMapping
    public List<BinaryContentResponseDto> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        return binaryContentService.findAllByIds(binaryContentIds);
    }
}
