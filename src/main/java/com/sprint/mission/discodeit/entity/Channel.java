package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChannelType type;

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // 1:N 관계
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    // 1:N 관계
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadStatus> readStatuses = new ArrayList<>();

    public void setUpdate(String newName, String newDescription) {
        if(this.type == ChannelType.PRIVATE){
            throw new UnsupportedOperationException("Private channels cannot be updated");
        }
        if(newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if(newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
    }

    //PUBLIC
    public Channel(String name, ChannelType type, String description) {
        super();
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Channel(ChannelType type) {
        super();
        this.type = type;
    }
}
