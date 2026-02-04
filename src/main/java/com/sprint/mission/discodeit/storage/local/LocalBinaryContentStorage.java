package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.*;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    @PostConstruct
    public void init(){
        try{
            if(!Files.exists(root)){
                Files.createDirectories(root);
                log.info("로컬 저장소 디렉토리 생성: {}", root.toAbsolutePath());
            }
        } catch(Exception e){
            log.error("로컬 저장소 초기화 실패", e);
            throw new BinaryContentInitFailedException(ErrorCode.BINARY_LOCAL_STORAGE_INIT_FAILED);
        }
    }

    private Path resolvePath(UUID id){
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID uuid, byte[] bytes) {
        try{
            Path filePath = resolvePath(uuid);
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.debug("파일 저장 완료: {}", filePath);
            return uuid;
        } catch(IOException e){
            log.error("파일 저장 실패: {}", uuid, e);
            throw new BinaryContentSaveFailException(ErrorCode.BINARY_CONTENT_SAVE_FAILED);
        }
    }

    @Override
    public InputStream get(UUID uuid) {
         try {
             Path filePath = resolvePath(uuid);
             if(!Files.exists(filePath)){
                 throw new BinaryContentNotFoundException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
             }
             return new FileInputStream(filePath.toFile());
         } catch(IOException e){
             log.error("파일 읽기 실패: {}", uuid, e);
             throw new BinaryContentBadRequestException(ErrorCode.BINARY_CONTENT_READ_FAILED);
         }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponseDto binaryContentResponseDto) {
        try {
            InputStream inputStream = get(binaryContentResponseDto.getId());
            Resource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(binaryContentResponseDto.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + binaryContentResponseDto.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(binaryContentResponseDto.getSize()))
                    .body(resource);
        } catch (Exception e){
            log.error("파일 다운로드 실패: {}", binaryContentResponseDto.getId(), e);
            throw new BinaryContentDownloadFailedException(ErrorCode.BINARY_CONTENT_DOWNLOAD_FAILED);
        }
    }
}
