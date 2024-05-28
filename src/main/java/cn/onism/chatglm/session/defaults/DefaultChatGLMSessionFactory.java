package cn.onism.chatglm.session.defaults;

import cn.onism.chatglm.IChatGLMApi;
import cn.onism.chatglm.constant.Model;
import cn.onism.chatglm.executor.Executor;
import cn.onism.chatglm.interceptor.HttpInterceptor;
import cn.onism.chatglm.session.ChatGLMSession;
import cn.onism.chatglm.session.ChatGLMSessionFactory;
import cn.onism.chatglm.session.Configuration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 默认会话工厂
 *
 * @author HeXin
 * @date 2024/03/27
 */
public class DefaultChatGLMSessionFactory implements ChatGLMSessionFactory {

    private final Configuration configuration;

    public DefaultChatGLMSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ChatGLMSession openSession() {
        // 1. 日志配置
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(configuration.getLevel());

        // 2. 开启 Http 客户端
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new HttpInterceptor(configuration))
                .connectTimeout(configuration.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(configuration.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(configuration.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        configuration.setHttpClient(okHttpClient);

        // 3. 创建 API 服务
        IChatGLMApi chatGLMApi = new Retrofit.Builder()
                .baseUrl(configuration.getApiHost())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(IChatGLMApi.class);

        configuration.setChatGLMApi(chatGLMApi);

        // 4. 实例化执行器
        Map<Model, Executor> executorGroup = configuration.newExecutorGroup();

        return new DefaultChatGLMSession(configuration, executorGroup);
    }
}
