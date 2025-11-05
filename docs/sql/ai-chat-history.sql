-- AI 客服会话表
CREATE TABLE IF NOT EXISTS ai_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_code VARCHAR(64) NOT NULL UNIQUE COMMENT '会话编码',
    title VARCHAR(100) COMMENT '会话标题',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_session_code (session_code),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客服会话';

-- AI 客服消息表
CREATE TABLE IF NOT EXISTS ai_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    session_id BIGINT NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: system/user/assistant',
    content TEXT NOT NULL COMMENT '消息内容',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    FOREIGN KEY (session_id) REFERENCES ai_session(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客服消息';
