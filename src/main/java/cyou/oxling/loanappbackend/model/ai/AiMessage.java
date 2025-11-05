package cyou.oxling.loanappbackend.model.ai;

import java.util.Date;
import lombok.Data;

@Data
public class AiMessage {
    private Long id;
    private Long sessionId;
    private String role;  // system, user, assistant
    private String content;
    private Date createdAt;
}
