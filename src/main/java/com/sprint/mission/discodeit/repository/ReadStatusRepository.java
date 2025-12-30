package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    // 유저-채널 읽음 상태 저장 : save

    // 특정 유저가 특정 채널에서 마지막으로 읽은 시점을 가져옴
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 채널 참여자별 미확인 메시지 수 계산
    List<ReadStatus> findByChannelId(UUID channelId);

    // 유저가 여러 채널을 어디까지 읽었는지 목록 조회
    List<ReadStatus> findByUserId(UUID userId);

    // 읽음 상태 업데이트
//    void update(ReadStatus readStatus);

    // 사용자가 채널을 떠난 경우 삭제 : delete

    // 특정 채널 읽음 상태 삭제, 채널 삭제 시 cascade
    void deleteAllByChannelId(UUID channelId);

    UUID userId(UUID userId);
}
