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
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    private String content; //내용 입력

    // N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; //작성채널

    // N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author; //작성자

    // N:M 관계
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments; //첨부파일

    public Message(Channel channel, User author, String content, List<BinaryContent> attachments) {
        super();
        this.channel = channel;
        this.author = author;
        this.content = content;
        this.attachments = (attachments != null) ? new ArrayList<>(attachments) : new ArrayList<>();
    }

    public void setUpdate(String newContent){
        if(newContent != null && !newContent.equals(this.content)){
            this.content = newContent;
        }
    }

    public void setAttachmentIds(List<BinaryContent> newAttachments) {
        this.attachments = (newAttachments != null) ? new ArrayList<>(newAttachments) : new ArrayList<>();
    }
}

