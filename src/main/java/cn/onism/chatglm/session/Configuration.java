package cn.onism.chatglm.session;

import cn.onism.chatglm.IChatGLMApi;
import cn.onism.chatglm.constant.CodeEnum;
import cn.onism.chatglm.constant.Model;
import cn.onism.chatglm.exception.CustomException;
import cn.onism.chatglm.executor.Executor;
import cn.onism.chatglm.executor.impl.NewExecutor;
import cn.onism.chatglm.executor.impl.OldExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.util.EnumMap;
import java.util.Map;

/**
 * 配置类
 *
 * @author HeXin
 * @date 2024/03/27
 */
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    /**
     * ChatGLM 请求 URL 地址
     */
    @Getter
    @Setter
    private String apiHost = "https://open.bigmodel.cn/";

    @Getter
    private String apiKey;

    @Getter
    private String apiSecret;

    /**
     * ChatGLM API
     */
    @Setter
    @Getter
    private IChatGLMApi chatGLMApi;

    @Getter
    @Setter
    private OkHttpClient httpClient;

    @Setter
    @Getter
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.HEADERS;

    /**
     * 连接超时
     */
    @Setter
    @Getter
    private long connectTimeout = 450L;
    /**
     * 写入超时
     */
    @Setter
    @Getter
    private long writeTimeout = 450L;
    /**
     * 读取超时
     */
    @Setter
    @Getter
    private long readTimeout = 450L;

    /**
     * 执行者组
     */
    private Map<Model, Executor> executorGroup;

    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecret = apiSecretKey;
        String[] arrStr = apiSecretKey.split("\\.");
        if (arrStr.length != 2) {
        throw new CustomException(CodeEnum.INVALID_API_SECRET_KEY);
        }
        this.apiKey = arrStr[0];
        this.apiSecret = arrStr[1];
    }

    public EventSource.Factory createRequestFactory() {
        return EventSources.createFactory(httpClient);
    }

    public Map<Model, Executor> newExecutorGroup() {
        this.executorGroup = new EnumMap<>(Model.class);
        // 旧版模型，兼容
        Executor glmOldExecutor = new OldExecutor(this);
        this.executorGroup.put(Model.CHATGLM_6B_SSE, glmOldExecutor);
        this.executorGroup.put(Model.CHATGLM_LITE, glmOldExecutor);
        this.executorGroup.put(Model.CHATGLM_LITE_32K, glmOldExecutor);
        this.executorGroup.put(Model.CHATGLM_STD, glmOldExecutor);
        this.executorGroup.put(Model.CHATGLM_PRO, glmOldExecutor);
        this.executorGroup.put(Model.CHATGLM_TURBO, glmOldExecutor);
        // 新版模型，配置
        Executor glmExecutor = new NewExecutor(this);
        this.executorGroup.put(Model.GLM_3_TURBO, glmExecutor);
        this.executorGroup.put(Model.GLM_4, glmExecutor);
        this.executorGroup.put(Model.GLM_4V, glmExecutor);
        this.executorGroup.put(Model.COGVIEW_3, glmExecutor);
        this.executorGroup.put(Model.CHARGLM_3, glmExecutor);
        this.executorGroup.put(Model.EMBEDDING_2, glmExecutor);
        return this.executorGroup;
    }
}
