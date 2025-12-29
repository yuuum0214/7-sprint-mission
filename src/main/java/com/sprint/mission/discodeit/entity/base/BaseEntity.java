package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass // 실제 DB에 매핑x, 자식 엔티티에 필드만 상속
public class BaseEntity {//implements Serializable {

//    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id; //객체 식별 id

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    public BaseEntity(){
        this.createdAt = Instant.now();
    }
}
