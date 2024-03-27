package cn.onism.chatglm.session;

/**
 * 会话工厂接口
 *
 * @author HeXin
 * @date 2024/03/27
 */
public interface ChatGLMSessionFactory {

    /**
     * 开启服务
     *
     * @return {@link ChatGLMSession}
     */
    ChatGLMSession openSession();
}
