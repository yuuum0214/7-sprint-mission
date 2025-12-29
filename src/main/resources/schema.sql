--- 존재하면 지우고 실행
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- 테이블
-- Users
CREATE TABLE users (
    id         UUID PRIMARY KEY,
    username   VARCHAR(50)               NOT NULL,
    email      VARCHAR(100)              NOT NULL,
    password   VARCHAR(60)               NOT NULL,
    created_at timestamp with time zone  NOT NULL,
    updated_at timestamp with time zone,
    profile_id UUID
);

-- Channels
CREATE TABLE channels (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100),
    description VARCHAR(500),
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    type VARCHAR(10) NOT NULL
);

-- Messages
CREATE TABLE messages (
    id         UUID PRIMARY KEY,
    content    TEXT,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    channel_id UUID                     NOT NULL,
    author_id  UUID
);

-- Binary contents
CREATE TABLE binary_contents (
    id           UUID PRIMARY KEY,
    file_name    VARCHAR(255)              NOT NULL,
    size         BIGINT                    NOT NULL,
    content_type VARCHAR(100)              NOT NULL,
    created_at   timestamp with time zone  NOT NULL
);

-- Message Attachments
CREATE TABLE message_attachments (
    message_id    UUID NOT NULL,
    attachment_id UUID NOT NULL,
    PRIMARY KEY (message_id, attachment_id)
);

-- User statuses
CREATE TABLE user_statuses (
    id             UUID PRIMARY KEY,
    user_id        UUID UNIQUE              NOT NULL,
    last_active_at timestamp with time zone NOT NULL,
    created_at     timestamp with time zone NOT NULL,
    updated_at     timestamp with time zone
);

-- Read status
CREATE TABLE read_statuses (
    id UUID PRIMARY KEY,
    created_at timestamp with time zone   NOT NULL,
    updated_at timestamp with time zone,
    user_id UUID                          NOT NULL,
    channel_id UUID                       NOT NULL,
    last_read_at timestamp with time zone NOT NULL,
    UNIQUE (user_id, channel_id)
);


----------
-- FK
----------

ALTER TABLE users
    ADD CONSTRAINT fk_users_binary_content
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents(id)
            ON DELETE SET NULL;

ALTER TABLE user_statuses
    ADD CONSTRAINT fk_user_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT fk_message_user
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE SET NULL;

ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachment_binary_content
        FOREIGN KEY (attachment_id)
            REFERENCES binary_contents (id)
            ON DELETE CASCADE;

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE;