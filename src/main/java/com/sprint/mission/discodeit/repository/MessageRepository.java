package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    // 메시지 저장 : save

//    Optional<Message> findById(Message message); // 메시지 조회

    //메시지 전체 조회 : findAll

    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    List<Message> findByChannelId(UUID channelId); // 채널에 속한 메시지의 파일 확인

    Optional<Instant> findLastByChannel(Channel channel); // 가장 마지막에 온 메시지

    // 메시지 삭제 : delete

    void deleteAllByChannelId(Channel channel); // 특정 채널의 모든 메시지 삭제
}