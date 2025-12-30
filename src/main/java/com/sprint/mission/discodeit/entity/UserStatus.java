package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt; //마지막 접속시간

    // 1:1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public UserStatus(User user){
        this(user, Instant.now());
    }

    public UserStatus(User user, Instant lastActiveAt) {
        super();
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }

    public void updateLastActiveAt(Instant newActiveAt) {
        this.lastActiveAt = newActiveAt;
    }

    public boolean isOnline(){
        return Duration.between(lastActiveAt, Instant.now()).toMinutes() <= 5;
    }

    public void userInternal(User user){
        this.user = user;
    }
}
