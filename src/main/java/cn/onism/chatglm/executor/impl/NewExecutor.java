package cn.onism.chatglm.executor.impl;

import cn.hutool.json.JSONUtil;
import cn.onism.chatglm.IChatGLMApi;
import cn.onism.chatglm.constant.CodeEnum;
import cn.onism.chatglm.constant.EventType;
import cn.onism.chatglm.constant.HttpEnum;
import cn.onism.chatglm.exception.CustomException;
import cn.onism.chatglm.executor.Executor;
import cn.onism.chatglm.executor.result.ResultHandler;
import cn.onism.chatglm.model.*;
import cn.onism.chatglm.session.Configuration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 智谱AI通用大模型<br>
 * 包含：glm-3-turbo、glm-4 等模型<br>
 * 文档地址：<a href="https://open.bigmodel.cn/dev/api">https://open.bigmodel.cn/dev/api</a>
 *
 * @author HeXin
 * @date 2024/03/27
 */
@Slf4j
public class NewExecutor implements Executor, ResultHandler {

    private final Configuration configuration;

    private final EventSource.Factory factory;

    public NewExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.factory = configuration.createRequestFactory();
        this.chatGLMApi = configuration.getChatGLMApi();
        this.httpClient = configuration.getHttpClient();
    }

    /**
     * ChatGLM 接口
     */
    private final IChatGLMApi chatGLMApi;

    private final OkHttpClient httpClient;
    @Override
    public EventSource completions(ChatRequest chatRequest, EventSourceListener eventSourceListener) {
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IChatGLMApi.V4))
                .post(RequestBody.create(MediaType.parse(HttpEnum.JSON_CONTENT_TYPE), chatRequest.toString()))
                .build();
        // 返回事件结果
        return factory.newEventSource(request, Boolean.TRUE.equals(chatRequest.getIsCompatible()) ? eventSourceListener(eventSourceListener) : eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(ChatRequest chatRequest) {
        // 用于执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder dataBuffer = new StringBuilder();

        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IChatGLMApi.V4))
                .post(RequestBody.create(MediaType.parse(HttpEnum.JSON_CONTENT_TYPE), chatRequest.toString()))
                .build();

        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                if ("[DONE]".equals(data)) {
                    log.info("[输出结束] Tokens {}", JSONUtil.toJsonStr(data));
                    return;
                }

                ChatResponse response = JSONUtil.toBean(data, ChatResponse.class);
                log.debug("测试结果：{}", JSONUtil.toJsonStr(response));
                List<ChatResponse.Choice> choices = response.getChoices();
                for (ChatResponse.Choice choice : choices) {
                    if (!"stop".equals(choice.getFinishReason())) {
                        dataBuffer.append(choice.getDelta().getContent());
                    }
                }

            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                future.complete(dataBuffer.toString());
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                future.completeExceptionally(new CustomException(CodeEnum.REQUEST_CLOESD));
            }
        });

        return future;
    }

    @Override
    public ChatSyncResponse completionsSync(ChatRequest chatRequest) {
        // sync 同步请求，stream 为 false
        chatRequest.setStream(false);
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IChatGLMApi.V4))
                .post(RequestBody.create(MediaType.parse(HttpEnum.JSON_CONTENT_TYPE), chatRequest.toString()))
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if(!response.isSuccessful()){
                throw new CustomException(CodeEnum.REQUEST_FAILED);
            }
            return JSONUtil.toBean(Objects.requireNonNull(response.body()).string(),ChatSyncResponse.class);
        } catch (IOException e) {
            throw new CustomException(e);
        }
    }

    /**
     * 生成图片
     *
     * @param imageRequest 图像请求
     * @return {@link ImageResponse}
     */
    @Override
    public ImageResponse generations(ImageRequest imageRequest) {
        return chatGLMApi.convertImage(imageRequest).blockingGet();
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                if ("[DONE]".equals(data)) {
                    return;
                }
                ChatResponse response = JSONUtil.toBean(data, ChatResponse.class);
                if (response.getChoices() != null && 1 == response.getChoices().size() && "stop".equals(response.getChoices().get(0).getFinishReason())) {
                    eventSourceListener.onEvent(eventSource, id, EventType.FINISH.value(),  data);
                    return;
                }
                eventSourceListener.onEvent(eventSource, id, EventType.ADD.value(), data);
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }
        };
    }
}
