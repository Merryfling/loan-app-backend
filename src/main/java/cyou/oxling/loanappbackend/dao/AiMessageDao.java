package cyou.oxling.loanappbackend.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cyou.oxling.loanappbackend.model.ai.AiMessage;

@Mapper
public interface AiMessageDao {
    
    void insert(AiMessage message);
    
    List<AiMessage> listBySessionId(@Param("sessionId") Long sessionId);
}
