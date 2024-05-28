package cn.onism.chatglm.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型常量
 *
 * @author HeXin
 * @date 2024/03/27
 */
@AllArgsConstructor
// 忽略一切警告
@SuppressWarnings("all")
public enum Model {
    @Deprecated
    CHATGLM_6B_SSE("chatGLM_6b_SSE", "ChatGLM-6B 测试模型"),
    @Deprecated
    CHATGLM_LITE("chatglm_lite", "轻量版模型，适用对推理速度和成本敏感的场景"),
    @Deprecated
    CHATGLM_LITE_32K("chatglm_lite_32k", "标准版模型，适用兼顾效果和成本的场景"),
    @Deprecated
    CHATGLM_STD("chatglm_std", "适用于对知识量、推理能力、创造力要求较高的场景"),
    @Deprecated
    CHATGLM_PRO("chatglm_pro", "适用于对知识量、推理能力、创造力要求较高的场景"),
    /** 智谱AI 23年06月发布 */
    CHATGLM_TURBO("chatglm_turbo", "适用于对知识量、推理能力、创造力要求较高的场景"),
    /** 智谱AI 24年01月发布 */
    GLM_3_TURBO("glm-3-turbo","适用于对知识量、推理能力、创造力要求较高的场景"),
    GLM_4("glm-4","适用于复杂的对话交互和深度内容创作设计的场景"),
    GLM_4V("glm-4v","根据输入的自然语言指令和图像信息完成任务，推荐使用 SSE 或同步调用方式请求接口"),
    COGVIEW_3("cogview-3","根据用户的文字描述生成图像,使用同步调用方式请求接口"),
    CHARGLM_3("charglm-3","支持基于人设的角色扮演、超长多轮的记忆、千人千面的角色对话，应用于拟人对话或游戏场景"),
    EMBEDDING_2("embedding-2","文本向量模型，将输入的文本信息进行向量化表示，以便于结合向量数据库为大模型提供外部知识库，提高大模型推理的准确性。"),
    ;
    private final String code;
    @Getter
    private final String description;

    public String value() {
        return code;
    }
}
