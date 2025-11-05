package cyou.oxling.loanappbackend.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cyou.oxling.loanappbackend.dao.AiMessageDao;
import cyou.oxling.loanappbackend.dao.AiSessionDao;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.ai.AiMessage;
import cyou.oxling.loanappbackend.model.ai.AiSession;
import cyou.oxling.loanappbackend.service.AiCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiCustomerServiceImpl implements AiCustomerService {

    private final ChatModel chatModel;
    private final AiSessionDao sessionDao;
    private final AiMessageDao messageDao;

    @Value("${ai.prompt.system:}")
    private String systemPrompt;

    @Override
    public ChatResult chat(Long userId, String sessionCode, String question) {
        // 1. 找到或创建会话
        AiSession session = findOrCreateSession(userId, sessionCode, question);

        // 2. 保存用户消息
        saveMessage(session.getId(), "user", question);

        // 3. 构建消息列表（system + 历史消息 + 当前问题）
        List<Message> messages = buildMessages(session.getId());

        // 4. 调用 AI
        String answer;
        try {
            answer = chatModel.call(new Prompt(messages))
                    .getResult()
                    .getOutput()
                    .getContent();
        } catch (Exception e) {
            log.error("AI调用失败", e);
            answer = "抱歉，当前咨询量较大，建议您稍后重试或联系人工客服。";
        }

        // 5. 保存 AI 回答
        saveMessage(session.getId(), "assistant", answer);

        // 6. 更新会话
        session.setUpdatedAt(new Date());
        sessionDao.update(session);

        return new ChatResult(session.getSessionCode(), answer);
    }

    @Override
    public List<AiSession> getSessions(Long userId) {
        return sessionDao.listByUserId(userId);
    }

    @Override
    public List<AiMessage> getMessages(Long userId, String sessionCode) {
        AiSession session = sessionDao.findBySessionCode(sessionCode);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该会话");
        }
        return messageDao.listBySessionId(session.getId());
    }

    private AiSession findOrCreateSession(Long userId, String sessionCode, String question) {
        if (sessionCode != null && !sessionCode.isBlank()) {
            AiSession session = sessionDao.findBySessionCode(sessionCode);
            if (session == null) {
                throw new BusinessException("会话不存在");
            }
            if (!session.getUserId().equals(userId)) {
                throw new BusinessException("无权访问该会话");
            }
            return session;
        }

        // 创建新会话
        AiSession session = new AiSession();
        session.setUserId(userId);
        session.setSessionCode(UUID.randomUUID().toString().replace("-", ""));
        session.setTitle(generateTitle(question));
        Date now = new Date();
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        sessionDao.insert(session);
        return session;
    }

    private String generateTitle(String question) {
        // 取前40个字符作为标题
        if (question.length() <= 40) {
            return question;
        }
        return question.substring(0, 40) + "...";
    }

    private AiMessage saveMessage(Long sessionId, String role, String content) {
        AiMessage msg = new AiMessage();
        msg.setSessionId(sessionId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setCreatedAt(new Date());
        messageDao.insert(msg);
        return msg;
    }

    private List<Message> buildMessages(Long sessionId) {
        List<Message> messages = new ArrayList<>();

        // 1. 添加 system prompt
        String prompt = systemPrompt != null && !systemPrompt.isBlank() 
                ? systemPrompt 
                : getDefaultSystemPrompt();
        messages.add(new SystemMessage(prompt));

        // 2. 添加历史消息
        List<AiMessage> history = messageDao.listBySessionId(sessionId);
        for (AiMessage msg : history) {
            switch (msg.getRole()) {
                case "user" -> messages.add(new UserMessage(msg.getContent()));
                case "assistant" -> messages.add(new AssistantMessage(msg.getContent()));
            }
        }

        return messages;
    }

    private String getDefaultSystemPrompt() {
        return """
                # 角色
                你是一名专业的贷款产品客服助手，为用户提供友好、准确的咨询服务。
                
                # 核心原则
                1. 用户友好：使用简单易懂的语言，避免专业术语
                2. 准确可靠：只提供确定的信息，不确定的建议联系人工
                3. 合规安全：不泄露内部技术细节、风控规则、系统参数
                4. 积极正面：保持专业、耐心、友善的服务态度
                
                # 产品信息
                
                ## 贷款基本信息
                - 贷款金额范围：1000元 - 200000元
                - 贷款期限：3个月、6个月、12个月、24个月
                - 利率：年化5%起（具体以审批结果为准）
                - 放款时间：审批通过后1-3个工作日
                
                ## 申请条件
                - 年龄：18-60周岁
                - 身份：中国大陆公民
                - 信用：征信记录良好，无严重逾期
                - 收入：有稳定的收入来源
                
                ## 申请流程
                1. 注册登录：手机号注册并完成实名认证
                2. 填写资料：完善个人信息、工作信息
                3. 上传证件：身份证、收入证明等
                4. 等待审核：系统自动审核+人工复核
                5. 签约放款：审核通过后签署电子合同，等待放款
                
                ## 还款相关
                - 还款方式：等额本息，按月还款
                - 还款渠道：App内还款、银行卡自动扣款
                - 提前还款：支持提前还款，无手续费
                - 逾期后果：产生逾期罚息，影响个人征信
                
                ## 常见问题
                
                ### 审核相关
                Q: 审核需要多久？
                A: 通常24小时内完成，高峰期可能延长至3个工作日。您可以在App中查看审核进度。
                
                Q: 为什么审核不通过？
                A: 可能原因包括：资料不完整、不符合申请条件、征信状况不佳等。具体原因可联系人工客服咨询。
                
                Q: 可以重新申请吗？
                A: 审核不通过后，建议30天后再次申请，期间可补充完善个人资料。
                
                ### 额度相关
                Q: 我能贷多少钱？
                A: 最终额度根据您的信用状况、收入水平等综合评估，提交申请后系统会给出具体额度。
                
                Q: 如何提升额度？
                A: 保持良好的还款记录、完善个人资料、增加收入证明都有助于提额。
                
                ### 费用相关
                Q: 有哪些费用？
                A: 主要是贷款利息（年化5%起），无其他隐藏费用。提前还款不收手续费。
                
                Q: 利息怎么算？
                A: 采用等额本息方式，每月还款金额相同。具体金额在申请时可查看还款计划。
                
                ### 账户相关
                Q: 忘记密码怎么办？
                A: 在登录页面点击"忘记密码"，通过手机验证码重置密码。
                
                Q: 如何注销账户？
                A: 需确保无未结清贷款，然后联系人工客服申请注销。
                
                # 回答规范
                
                ## 应该做的
                - 简洁明了：直接回答问题，不冗长
                - 分点列举：复杂内容用序号或分点说明
                - 提供建议：给出下一步操作指引
                - 保持专业：使用礼貌用语，"您好"、"请"、"谢谢"
                
                ## 不应该做的
                - 不要说：审核算法、风控模型、评分规则、数据库字段
                - 不要说：系统bug、技术故障、代码逻辑
                - 不要说：具体的信用分数阈值、拒绝原因详细参数
                - 不要说：竞品信息、行业内幕
                - 不要承诺：一定能过、保证下款、特殊通道
                
                ## 特殊情况处理
                如遇以下情况，建议用户联系人工客服：
                - 投诉建议
                - 账户异常
                - 还款纠纷
                - 信息变更（手机号、银行卡等）
                - 复杂业务咨询
                - 用户情绪激动或态度恶劣
                """;
    }
}
