package cn.onism.chatglm.executor.impl;

import cn.hutool.json.JSONUtil;
import cn.onism.chatglm.IChatGLMApi;
import cn.onism.chatglm.constant.CodeEnum;
import cn.onism.chatglm.constant.EventType;
import cn.onism.chatglm.constant.HttpEnum;
import cn.onism.chatglm.exception.CustomException;
import cn.onism.chatglm.executor.Executor;
import cn.onism.chatglm.model.*;
import cn.onism.chatglm.session.Configuration;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 智谱AI旧版接口模型执行器<br>
 * 包括：chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro<br>
 * 文档地址：<a href="https://open.bigmodel.cn/dev/api">https://open.bigmodel.cn/dev/api</a>
 *
 * @author HeXin
 * @date 2024/03/27
 */
public class OldExecutor implements Executor{

    /**
     * 配置
     */
    private final Configuration configuration;

    /**
     * 工厂
     */
    private final EventSource.Factory factory;

    public OldExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.factory = configuration.createRequestFactory();
    }

    @Override
    public EventSource completions(ChatRequest chatRequest, EventSourceListener eventSourceListener) {
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IChatGLMApi.V3_COMPLETIONS).replace("{model}", chatRequest.getModel().value()))
                .post(RequestBody.create(MediaType.parse(HttpEnum.APPLICATION_JSON), chatRequest.toString()))
                .build();

        // 返回事件结果
        return factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(ChatRequest chatRequest) {
        // 用于执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder dataBuffer = new StringBuilder();

        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IChatGLMApi.V3_COMPLETIONS).replace("{model}", chatRequest.getModel().value()))
                .post(RequestBody.create(MediaType.parse(HttpEnum.APPLICATION_JSON), chatRequest.toString()))
                .build();

        // 异步响应请求
        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                ChatResponse response = JSONUtil.toBean(data, ChatResponse.class);
                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.ADD.value().equals(type)) {
                    dataBuffer.append(response.getData());
                } else if (EventType.FINISH.value().equals(type)) {
                    future.complete(dataBuffer.toString());
                }
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;
    }

    @Override
    public ChatSyncResponse completionsSync(ChatRequest chatRequest) {
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IChatGLMApi.V3_COMPLETIONS_SYNC).replace("{model}", chatRequest.getModel().value()))
                .header("Accept",HttpEnum.APPLICATION_JSON)
                .post(RequestBody.create(MediaType.parse(HttpEnum.APPLICATION_JSON), chatRequest.toString()))
                .build();
        OkHttpClient okHttpClient = configuration.getHttpClient();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new CustomException(e);
        }
        if(!response.isSuccessful()){
            throw new CustomException(CodeEnum.REQUEST_FAILED);
        }
        try {
            return JSONUtil.toBean(Objects.requireNonNull(response.body()).string(),ChatSyncResponse.class);
        } catch (IOException e) {
            throw new CustomException(e);
        }
    }

    @Override
    public ImageResponse generations(ImageRequest imageRequest) {
        throw new CustomException(CodeEnum.OLD_NOT_SUPPORTED);
    }
}
