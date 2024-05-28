package cn.onism.chatglm;


import cn.onism.chatglm.model.*;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * ChatGLM相关接口
 *
 * @author HeXin
 * @date 2024/03/26
 */
public interface IChatGLMApi {
    /**
     * GLM-3 SSE调用
     */
    String V3_COMPLETIONS = "api/paas/v3/model-api/{model}/sse-invoke";
    /**
     * GLM-3 同步调用
     */
    String V3_COMPLETIONS_SYNC = "api/paas/v3/model-api/{model}/invoke";


    /**
     * ChatGLM-4
     */
    String V4 = "api/paas/v4/chat/completions";

    /**
     * 超拟人大模型<br>
     *支持基于人设的角色扮演、超长多轮的记忆、千人千面的角色对话
     */
    String V3_CHARGLM = "api/paas/v3/model-api/charglm-3/sse-invoke";

    /**
     * 向量模型
     */
    String EMBEDDING = "api/paas/v4/embeddings";

    /**
     * 图像大模型
     */
    String COGVIEW3 = "api/paas/v4/images/generations";

    /**
     * 转换图像
     *
     * @param imageRequest 图像请求
     * @return {@link Single}<{@link ImageResponse}>
     */
    @POST(COGVIEW3)
    Single<ImageResponse> convertImage(@Body ImageRequest imageRequest);

    /**
     * 完成
     *
     * @param model       模型
     * @param chatRequest 聊天请求
     * @return {@link Single}<{@link ChatResponse}>
     */
    @POST(V3_COMPLETIONS)
    Single<ChatResponse> completions(@Path("model") String model, @Body ChatRequest chatRequest);

    /**
     * 完成
     *
     * @param chatRequest 聊天请求
     * @return {@link Single}<{@link ChatSyncResponse}>
     */
    @POST(V3_COMPLETIONS_SYNC)
    Single<ChatSyncResponse> completions(@Body ChatRequest chatRequest);
}
