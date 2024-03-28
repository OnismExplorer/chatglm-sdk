package cn.onism.chatglm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 通用大模型同步响应结果
 *
 * @author HeXin
 * @date 2024/03/27
 */
@Data
public class ChatSyncResponse {
    /**
     * 法典
     */
    private Integer code;
    /**
     * 味精
     */
    private String msg;
    /**
     * 成功
     */
    private Boolean success;
    /**
     * 数据
     */
    private ChatGLMData data;

    // 24年1月发布模型新增字段 GLM3、GLM4
    @JsonProperty("task_status")
    private String taskStatus;
    /**
     * 选择
     */
    private List<Choice> choices;

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
        this.data = new ChatGLMData();
        this.data.setChoices(choices);
    }


    @Data
    public static class ChatGLMData {
        /**
         * 选择
         */
        private List<Choice> choices;
        /**
         * 任务状态
         */
        @JsonProperty("task_status")
        private String taskStatus;
        /**
         * token 用量
         */
        private Usage usage;
        /**
         * 任务 ID
         */
        @JsonProperty("task_id")
        private String taskId;
        /**
         * 请求 ID
         */
        @JsonProperty("request_id")
        private String requestId;
    }

    @Data
    public static class Usage {
        /**
         * 完成 token 数
         */
        @JsonProperty("completion_tokens")
        private int completionTokens;
        /**
         * 提示 token 数
         */
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        /**
         * token 总数
         */
        @JsonProperty("total_tokens")
        private int totalTokens;
    }

    @Data
    public static class Choice {

        /**
         * 角色
         */
        private String role;
        /**
         * 内容
         */
        private String content;

        /**
         * 完成原因
         */
        @JsonProperty("finish_reason")
        private String finishReason;
        /**
         * 指数
         */
        private int index;
        /**
         * 消息
         */
        private Message message;
    }

    @Data
    public static class Message {
        /**
         * 角色
         */
        private String role;
        /**
         * 内容
         */
        private String content;
    }
}
