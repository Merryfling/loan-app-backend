package cyou.oxling.loanappbackend.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cyou.oxling.loanappbackend.model.ai.AiSession;

@Mapper
public interface AiSessionDao {
    
    void insert(AiSession session);
    
    void update(AiSession session);
    
    AiSession findBySessionCode(@Param("sessionCode") String sessionCode);
    
    List<AiSession> listByUserId(@Param("userId") Long userId);
}
