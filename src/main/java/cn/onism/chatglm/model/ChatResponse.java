package cn.onism.chatglm.model;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用大模型响应结果
 *
 * @author HeXin
 * @date 2024/03/27
 */
@Data
public class ChatResponse {
    // 旧版获得的数据方式
    private String data;
    private String meta;

    // 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
    private String id;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    // 封装 setChoices 对 data 属性赋值，兼容旧版使用方式
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
        for (Choice choice : choices) {
            if ("stop".equals(choice.finishReason)) {
                continue;
            }
            if (null == this.data) {
                this.data = "";
            }
            this.data = this.data.concat(choice.getDelta().getContent());
        }
    }

    // 封装 setChoices 对 meta 属性赋值，兼容旧版使用方式
    public void setUsage(Usage usage) {
        this.usage = usage;
        if (null != usage) {
            this.meta = JSONUtil.toJsonStr(Meta.builder().usage(usage).build());
        }
    }

    @Data
    public static class Choice {
        private Long index;
        @JsonProperty("finish_reason")
        private String finishReason;
        private Delta delta;
    }

    @Data
    public static class Delta {
        private String role;
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        /**
         * 任务状态
         */
        private String task_status;
        /**
         * token 使用量
         */
        private Usage usage;
        /**
         * 任务 ID
         */
        private String task_id;
        /**
         * 请求 ID
         */
        private String request_id;
    }

    @Data
    public static class Usage {
        /**
         * 完成令牌
         */
        private int completion_tokens;
        /**
         * 提示令牌
         */
        private int prompt_tokens;
        /**
         * token 总数
         */
        private int total_tokens;
    }
}
