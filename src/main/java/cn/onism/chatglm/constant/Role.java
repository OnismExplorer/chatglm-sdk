package cn.onism.chatglm.constant;

import lombok.AllArgsConstructor;

/**
 * 角色
 *
 * @author HeXin
 * @date 2024/03/27
 */
@AllArgsConstructor
public enum Role {
    /**
     * user 用户输入的内容
     */
    USER("user"),
    /**
     * 模型生成的内容
     */
    ASSISTANT("assistant"),

    /**
     * 系统
     */
    SYSTEM("system"),

    ;
    private final String code;

    public String value() {
        return code;
    }
}
