package cn.onism.chatglm.interceptor;

import cn.onism.chatglm.constant.HttpEnum;
import cn.onism.chatglm.session.Configuration;
import cn.onism.chatglm.utils.JwtTokenUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class HttpInterceptor implements Interceptor {

    /**
     * 配置类
     */
    private final Configuration configuration;

    public HttpInterceptor(Configuration configuration) {
        this.configuration = configuration;
    }
    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        // 获取原始 Request 请求
        Request original = chain.request();

        // 构建请求
        Request request = original.newBuilder()
                .url(original.url())
                .header("Authorization", "Bearer " + JwtTokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret()))
                .header("Content-Type", HttpEnum.JSON_CONTENT_TYPE)
                .header("User-Agent", HttpEnum.DEFAULT_USER_AGENT)
//                .header("Accept", null != original.header("Accept") ? Objects.requireNonNull(original.header("Accept")) : HttpEnum.SSE_CONTENT_TYPE)
                .method(original.method(), original.body())
                .build();

        // 返回执行结果
        return chain.proceed(request);
    }
}