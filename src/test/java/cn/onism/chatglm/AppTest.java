package cn.onism.chatglm;

import cn.hutool.json.JSONUtil;
import cn.onism.chatglm.constant.CodeEnum;
import cn.onism.chatglm.constant.EventType;
import cn.onism.chatglm.constant.Model;
import cn.onism.chatglm.constant.Role;
import cn.onism.chatglm.exception.CustomException;
import cn.onism.chatglm.model.ChatRequest;
import cn.onism.chatglm.model.ChatResponse;
import cn.onism.chatglm.session.ChatGLMSession;
import cn.onism.chatglm.session.Configuration;
import cn.onism.chatglm.session.defaults.DefaultChatGLMSessionFactory;
import cn.onism.chatglm.utils.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.io.Serial;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 单元测试
 */
@Slf4j
public class AppTest {

    private ChatGLMSession chatGLMSession;

    @Before
    public void testChatGLMSessionFactory(){
        // 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("YOUR_API_KEY");
        //记录请求和响应行及其各自的标头和正文（如果存在）
        configuration.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 新建会话工厂
        DefaultChatGLMSessionFactory factory = new DefaultChatGLMSessionFactory(configuration);
        // 开启会话
        this.chatGLMSession = factory.openSession();
    }

    @Test
    public void test_completions() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 入参；模型、请求信息
        ChatRequest request = new ChatRequest();
        request.setModel(Model.GLM_3_TURBO); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setIncremental(false);
        request.setIsCompatible(true); // 是否对返回结果数据做兼容，24年1月发布的 GLM_3_5_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异。开启 true 可以做兼容。
        // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
        request.setTools(new ArrayList<>() {

            @Serial
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatRequest.Tool.builder()
                        .type(ChatRequest.Tool.Type.WEB_SEARCH)
                        .webSearch(ChatRequest.Tool.WebSearch.builder().enable(true).searchQuery("Java快速排序").build())
                        .build());
            }
        });
        request.setPrompt(new ArrayList<>() {
            @Serial
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatRequest.Prompt.builder()
                        .role(Role.USER.value())
                        .content("帮我写一个Java快速排序")
                        .build());
            }
        });

        // 请求
        chatGLMSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                ChatResponse response = JSONUtil.toBean(data, ChatResponse.class);
                log.info("测试结果 onEvent：{}", response.getData());
                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.FINISH.value().equals(type)) {
                    ChatResponse.Meta meta = JSONUtil.toBean(response.getMeta(), ChatResponse.Meta.class);
                    log.info("[输出结束] Tokens {}", JSONUtil.toJsonStr(meta));
                }
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.info("对话异常");
                countDownLatch.countDown();
            }
        });

        // 等待
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testApp(){

    }

    @Test
    public void testJson(){
        Object object = 999L;
        System.out.println(EncryptUtils.encrypt(JSONUtil.toJsonStr(object)));
    }

    @Test
    public void testException(){
        throw new CustomException(CodeEnum.FAIL);
    }
}