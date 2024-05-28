package cn.onism.chatglm.executor.result;

import okhttp3.sse.EventSourceListener;

/**
 * 结果处理器
 *
 * @author HeXin
 * @date 2024/03/27
 */
public interface ResultHandler {

    /**
     * 事件源侦听器
     *
     * @param eventSourceListener 事件源侦听器
     * @return {@link EventSourceListener}
     */
    EventSourceListener eventSourceListener(EventSourceListener eventSourceListener);
}
