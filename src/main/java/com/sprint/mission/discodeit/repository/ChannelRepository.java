package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    @Query("SELECT c FROM Channel c JOIN c.readStatuses rs WHERE rs.user.id = :userId")
    List<Channel> findAllByUserId(UUID userId);

    @Query("SELECT DISTINCT c FROM Channel c LEFT JOIN FETCH c.readStatuses rs LEFT JOIN FETCH rs.user WHERE c.id = :id")
    Optional<Channel> findByIdWithParticipants(UUID id);

    @Query("SELECT DISTINCT c FROM Channel c LEFT JOIN FETCH c.readStatuses rs LEFT JOIN FETCH rs.user")
    List<Channel> findAllWithParticipants();

}