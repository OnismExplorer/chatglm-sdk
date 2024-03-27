package cn.onism.chatglm.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型
 *
 * @author HeXin
 * @date 2024/03/27
 */
@AllArgsConstructor
public enum EventType {
    ADD("add", "增量"),
    FINISH("finish", "结束"),
    ERROR("error", "错误"),
    INTERRUPTED("interrupted", "中断"),

    ;
    private final String code;
    @Getter
    private final String description;

    public String value() {
        return code;
    }
}
