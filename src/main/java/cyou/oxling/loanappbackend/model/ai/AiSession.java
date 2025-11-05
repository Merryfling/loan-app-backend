package cyou.oxling.loanappbackend.model.ai;

import java.util.Date;
import lombok.Data;

@Data
public class AiSession {
    private Long id;
    private Long userId;
    private String sessionCode;
    private String title;
    private Date createdAt;
    private Date updatedAt;
}
