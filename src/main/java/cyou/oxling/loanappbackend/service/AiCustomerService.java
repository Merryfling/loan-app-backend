package cyou.oxling.loanappbackend.service;

import java.util.List;

import cyou.oxling.loanappbackend.model.ai.AiMessage;
import cyou.oxling.loanappbackend.model.ai.AiSession;

public interface AiCustomerService {
    
    /**
     * AI 客服对话
     * @param userId 用户ID
     * @param sessionCode 会话ID（可选，为空则创建新会话）
     * @param question 用户问题
     * @return 包含会话ID和回答的结果
     */
    ChatResult chat(Long userId, String sessionCode, String question);
    
    /**
     * 获取用户的会话列表
     */
    List<AiSession> getSessions(Long userId);
    
    /**
     * 获取会话的历史消息
     */
    List<AiMessage> getMessages(Long userId, String sessionCode);
    
    record ChatResult(String sessionCode, String answer) {}
}
