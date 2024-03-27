package cn.onism.chatglm.session.defaults;

import cn.onism.chatglm.constant.CodeEnum;
import cn.onism.chatglm.constant.Model;
import cn.onism.chatglm.exception.CustomException;
import cn.onism.chatglm.executor.Executor;
import cn.onism.chatglm.model.ChatRequest;
import cn.onism.chatglm.model.ChatSyncResponse;
import cn.onism.chatglm.model.ImageRequest;
import cn.onism.chatglm.model.ImageResponse;
import cn.onism.chatglm.session.ChatGLMSession;
import cn.onism.chatglm.session.Configuration;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 默认会话服务
 *
 * @author HeXin
 * @date 2024/03/27
 */
public class DefaultChatGLMSession implements ChatGLMSession {

    private final Configuration configuration;
    private final Map<Model, Executor> executorGroup;

    public DefaultChatGLMSession(Configuration configuration, Map<Model, Executor> executorGroup) {
        this.configuration = configuration;
        this.executorGroup = executorGroup;
    }

    @Override
    public EventSource completions(ChatRequest chatRequest, EventSourceListener eventSourceListener) {
        Executor executor = executorGroup.get(chatRequest.getModel());
        if (null == executor) throw new CustomException(CodeEnum.EXECUTOR_NOT_IMPLEMENTED);
        return executor.completions(chatRequest, eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(ChatRequest chatRequest) {
        Executor executor = executorGroup.get(chatRequest.getModel());
        if (null == executor) throw new CustomException(CodeEnum.EXECUTOR_NOT_IMPLEMENTED);
        return executor.completions(chatRequest);
    }

    @Override
    public ChatSyncResponse completionsSync(ChatRequest chatRequest) {
        Executor executor = executorGroup.get(chatRequest.getModel());
        if (null == executor) throw new CustomException(CodeEnum.EXECUTOR_NOT_IMPLEMENTED);
        return executor.completionsSync(chatRequest);
    }

    @Override
    public ImageResponse covertImages(ImageRequest imageRequest) {
        Executor executor = executorGroup.get(imageRequest.getModelEnum());
        if (null == executor) throw new CustomException(CodeEnum.EXECUTOR_NOT_IMPLEMENTED);
        return executor.generations(imageRequest);
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }
}
