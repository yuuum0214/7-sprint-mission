package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    //유저 저장 : save
//    Optional<User> findById(UUID uuid); // 특정 유저 조회
//    Optional<User> findById(UUID uuid);
    // 유저 전체 조회 : findAll
    // UUID로 유저 삭제 : delete

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}