package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface BinaryContentApi {

    @GetMapping("/{binaryContentId}")
    @Operation(summary = "첨부 파일 조회", operationId = "find")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = BinaryContentResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("BinaryContent with id {binaryContentId} not found")
                    )
            )
    })
    public ResponseEntity<BinaryContentResponseDto> findById(
            @Parameter(description = "조회할 첨부 파일 ID")
            @PathVariable("binaryContentId") UUID uuid
    );


    @GetMapping
    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 목록 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = BinaryContentResponseDto.class)
                            )
                    )
            )
    })
    public List<BinaryContentResponseDto> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    );
}
