package cyou.oxling.loanappbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.model.ai.AiMessage;
import cyou.oxling.loanappbackend.model.ai.AiSession;
import cyou.oxling.loanappbackend.service.AiCustomerService;
import cyou.oxling.loanappbackend.service.AiCustomerService.ChatResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiCustomerController {

    private final AiCustomerService aiCustomerService;

    /**
     * 对话接口
     */
    @PostMapping("/chat")
    public Result<ChatResult> chat(
            @RequestAttribute("userId") Long userId,
            @RequestBody ChatRequest request) {
        if (request.question == null || request.question.isBlank()) {
            return Result.error("问题不能为空");
        }
        if (request.question.length() > 500) {
            return Result.error("问题长度不能超过500字");
        }
        ChatResult result = aiCustomerService.chat(userId, request.sessionCode, request.question);
        return Result.success(result);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/sessions")
    public Result<List<AiSession>> getSessions(@RequestAttribute("userId") Long userId) {
        List<AiSession> sessions = aiCustomerService.getSessions(userId);
        return Result.success(sessions);
    }

    /**
     * 获取会话历史消息
     */
    @GetMapping("/sessions/{sessionCode}/messages")
    public Result<List<AiMessage>> getMessages(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionCode) {
        List<AiMessage> messages = aiCustomerService.getMessages(userId, sessionCode);
        return Result.success(messages);
    }

    public record ChatRequest(String sessionCode, String question) {}
}
