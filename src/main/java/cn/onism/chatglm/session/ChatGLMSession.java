package cn.onism.chatglm.session;

import cn.onism.chatglm.model.ChatRequest;
import cn.onism.chatglm.model.ChatSyncResponse;
import cn.onism.chatglm.model.ImageRequest;
import cn.onism.chatglm.model.ImageResponse;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.concurrent.CompletableFuture;

/**
 * 会话服务接口
 *
 * @author HeXin
 * @date 2024/03/27
 */
public interface ChatGLMSession {
    EventSource completions(ChatRequest chatRequest, EventSourceListener eventSourceListener);

    CompletableFuture<String> completions(ChatRequest chatRequest);

    ChatSyncResponse completionsSync(ChatRequest chatRequest);

    ImageResponse covertImages(ImageRequest imageRequest);

    Configuration configuration();
}
