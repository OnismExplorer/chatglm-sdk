package cn.onism.chatglm.constant;

import lombok.Getter;

/**
 * 系统状态常量
 * @author HeXin
 * @date 2024/03/27
 */
@Getter
public enum CodeEnum {
    /**
     * 成功标志
     */
    SUCCESS(200, "成功！"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(502, "系统异常！"),
    /**
     * 服务异常
     */
    FAIL(500, "服务异常！"),
    INVALID_API_SECRET_KEY(401, "无效的API密钥！"),
    PROMPT_EMPTY(322,"消息或提示之一不能为空！"),
    REQUEST_CLOESD(323, "请求在完成前被关闭"),
    REQUEST_FAILED(324,"请求失败！"),
    OLD_NOT_SUPPORTED(333,"旧版本不支持文生图！"),
    EXECUTOR_NOT_IMPLEMENTED(335, "模型执行器尚未实现！");
    /**
     * 错误代码
     */
    private final int code;
    /**
     * 错误信息
     */
    private final String message;

    CodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
