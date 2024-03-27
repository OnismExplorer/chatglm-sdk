package cn.onism.chatglm.executor;

import cn.onism.chatglm.model.ChatRequest;
import cn.onism.chatglm.model.ChatSyncResponse;
import cn.onism.chatglm.model.ImageRequest;
import cn.onism.chatglm.model.ImageResponse;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.concurrent.CompletableFuture;

/**
 * 执行者接口
 *
 * @author HeXin
 * @date 2024/03/27
 */
public interface Executor {

    /**
     * 问答模式，流式反馈
     *
     * @param chatRequest 请求信息
     * @param eventSourceListener   实现监听；通过监听的 onEvent 方法接收数据
     * @return {@link EventSource} 响应结果
     */
    EventSource completions(ChatRequest chatRequest, EventSourceListener eventSourceListener);

    /**
     * 问答模式，同步反馈 —— 用流式转化 Future
     *
     * @param chatRequest 请求信息
     * @return {@link CompletableFuture}<{@link String}> 响应结果
     */
    CompletableFuture<String> completions(ChatRequest chatRequest);

    /**
     * 同步应答接口
     *
     * @param chatRequest 请求信息
     * @return {@link ChatSyncResponse}
     */
    ChatSyncResponse completionsSync(ChatRequest chatRequest);

    /**
     * 图片生成接口
     *
     * @param imageRequest 请求信息
     * @return {@link ImageResponse}
     */
    ImageResponse generations(ImageRequest imageRequest);
}
