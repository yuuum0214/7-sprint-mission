package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용을 위한 기본 생성자 추가
public class User extends BaseUpdatableEntity {

    //    private String userId; //가입 Id
    @Column(length = 60, nullable = false)
    private String password; //비밀번호

    @Column(length = 100, nullable = false, unique = true)
    private String email; //이메일

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String userName; //유저 이름

    //    private UUID profileImageId; //프로필
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    // 1:1 관계
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) // TODO: CASCADE.ALL , orphanRemoval 왜 쓰는지 공부할것! by 태언
    private UserStatus userStatus;

    public User(String password, String email, String userName, BinaryContent profile) {
        super();
        this.password = password;
        this.email = email;
        this.userName = userName;
        this.profile = profile;
    }

    public void setPassword(String newPassword) {
        if (newPassword != null && !newPassword.equals(this.password)) { //비밀번호 변경
            this.password = newPassword;
        }
    }

    public void setEmail(String newEmail) {
        if (newEmail != null && !newEmail.equals(this.email)) { //이메일 변경
            this.email = newEmail;
        }
    }

    public void setUserName(String newUserName) {
        if (newUserName != null && !newUserName.equals(this.userName)) {
            this.userName = newUserName;
        }
    }

    public void changeProfile(BinaryContent newProfile) {
        if (newProfile != null && !newProfile.equals(this.profile)) { //프로필 변경
            this.profile = newProfile;
        }
    }

    public void changeStatus(UserStatus newUserStatus) { // TODO: setUserStatus로 쓰지말고 편의 메서드 방식으로 리펙토링 해서 쓸것 by 태언
        if(this.userStatus != null){
            this.userStatus.userInternal(null);
        }

        this.userStatus = newUserStatus;

        if(newUserStatus != null && newUserStatus.getUser() != this){
            newUserStatus.userInternal(this);
        }
    }
}
