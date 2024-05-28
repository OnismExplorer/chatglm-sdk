# 智谱清言 AI 大模型 Java - SDK
&emsp;&emsp;本组件是为了让使用 Java 开发的(当然最先是为了我自己)同学们能够更方便接入并使用[智谱轻言 AI 大模型](https://www.zhipuai.cn/)(俗称 “ChatGLM”)。<br>
&emsp;&emsp;本 SDK 设计时借鉴了小傅哥开发的他的版本的开源SDK(github地址：[https://github.com/fuzhengwei/chatglm-sdk-java](https://github.com/fuzhengwei/chatglm-sdk-java))，并在此基础上添加了自定义异常处理，将其中部分代码格式化分离处理，并将获取到的个人apikey进行加密处理，保障个人资源信息安全。<br>
&emsp;&emsp;通过 Session 会话模型，使用`工厂模式`创建服务(模仿 MyBatis 源码设计)，方便扩展与后期维护。<br>

## 开始使用
### 申请ApiKey
&emsp;&emsp;首先需要在智谱AI官网申请一个自己个人的ApiKey，注册申请开通即可。申请地址：[https://open.bigmodel.cn/usercenter/apikeys](https://open.bigmodel.cn/usercenter/apikeys)<br>
&emsp;&emsp;本组件开发环境为 JDK 17，建议使用于 JDK 17 版本及以上开发环境。<br>
### 安装依赖
&emsp;&emsp;拉取本仓库代码，下载到本地，使用 `gradle publishToMavenLocal `安装到本地依赖中，然后在需要引入的项目/系统的 build.gradle 文件中引入下面代码：
```groovy
// 设置仓库(一定要设置本地仓库，不然无法读取到该依赖，如果已经设置了可忽略)
repositories {
    mavenLocal() // 本地仓库
}

dependencies {
    implementation 'cn.onism.chatglm:chatglm-sdk:1.0'
}
```
目前支持的模型有 chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro、chatglm_turbo、glm-3-turbo、glm-4、glm-4v、cogview-3、charglm-3(目前还没有接入功能)、embedding-2(目前还没有接入功能)，其中前五种模型在最新的官方中已经标为弃用，所以不建议使用。(但是考虑到兼容问题，组件依然加上了这些模型)<br>

### 环境配置
&emsp;&emsp;目前使用 Java 开发的项目基本上都是基于 SpringBoot/Cloud 进行的，所以为了方便使用，组件中也支持通过配置类的方式进行配置引入(插拔式)，更加方便快捷<br>

> SpringBoot 配置类

参数类
```java
@Data
@Configuration
@ConfigurationProperties(prefix = "chatglm.config", ignoreInvalidFields = true)
public class ChatGLMConfigProperties {
    /** 状态；open = 开启、close 关闭 */
    private boolean enabled;
    /** 转发地址 */
    private String apiHost;
    /** 可以申请 sk-*** */
    private String apiSecretKey;
}
```

配置类
 ```java
@Configuration
public class ChatGLMConfig {
    @Bean
    @ConditionalOnProperty(value = "chatglm.config.enabled", havingValue = "true")
    public ChatGLMSession openAiSession(ChatGLMConfigProperties properties) {
        // 1. 配置文件
        cn.onism.chatglm.session.Configuration configuration = new cn.onism.chatglm.session.Configuration();
        configuration.setApiHost(properties.getApiHost());
        configuration.setApiSecretKey(properties.getApiSecretKey());

        // 2. 会话工厂
        ChatGLMSessionFactory factory = new DefaultChatGLMSessionFactory(configuration);

        // 3. 开启会话
        return factory.openSession();
    }
}
```

&emsp;&emsp;添加好这两个配置类之后，就可以在 SpringBoot 的 application 配置文件中配置相关参数(这里以 yml 文件为例)，其中最重要的是上面申请到的ApiKey
```yaml
chatglm:
  config:
    # 状态；true = 开启、false 关闭
    enabled: true
    # 官网地址
    api-host: https://open.bigmodel.cn/
    # 官网申请 https://open.bigmodel.cn/usercenter/apikeys
    api-secret-key: $$$YOUR_API_KEY$$$
```

- 注意：如果在文件中配置关闭了SDK(enabled = false)，则通过依赖注入的 ChatGLMSession 为 null
```java
@Autowired(required = false)
private  ChatGLMSession chatGLMSession; // this is null
```

### 测试功能
下面将演示组件是具体如何调用智谱API，并封装返回结果的：
> 环境准备

因为这里演示环境并不是 SpringBoot 环境下，所以需要自己手动准备环境
```java
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
```

#### 同步请求
下面将演示同步请求，其中包括了`普通`和`future`两种模式

> 普通模式

```java
@Test
public void testCompletionsSync() {
    // 添加参数：模型、请求信息
    ChatRequest request = new ChatRequest();
    request.setModel(Model.GLM_4V); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
    request.setPrompt(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 190092826372819L;
        {
            add(ChatRequest.Prompt.builder()
                    .role(Role.USER.value())
                    .content("写一个Java快速排序")
                    .build());
        }
    });
    // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
    request.setTools(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 2993887281902891L;
        {
            add(ChatRequest.Tool.builder()
                    .type(ChatRequest.Tool.Type.WEB_SEARCH)
                    .webSearch(ChatRequest.Tool.WebSearch.builder().enable(true).searchQuery("快速排序").build())
                    .build());
        }
    });
    ChatSyncResponse response = chatGLMSession.completionsSync(request);
    log.info("测试结果：{}", JSONUtil.toJsonStr(response));
}
```
> Future 模式


```java
@Test
public void testCompletionsFuture() throws Exception {
    // 添加参数：模型、请求信息
    ChatRequest request = new ChatRequest();
    request.setModel(Model.CHATGLM_TURBO); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
    request.setPrompt(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 3790289172931192L;

        {
            add(ChatRequest.Prompt.builder()
                    .role(Role.USER.value())
                    .content("1+1")
                    .build());
        }
    });
    CompletableFuture<String> future = chatGLMSession.completions(request);
    String response = future.get();
    log.info("测试结果：{}", response);
}
```

#### 流式对话
&emsp;&emsp;流式对话：信息以流的方式传递，通常是逐条消息或逐句话。这种对话形式允许参与者在对话过程中实时地发送和接收信息，而不是像传统的对话形式那样，等待对方完成一系列的发言后才能回应。<br>
&emsp;&emsp;目前市面上的文本AI大多都是采用流式对话的方式进行响应，其优点在于，不会让人“死等”，以为程序卡住了，对用户的使用和阅读很友好

```java
/**
 * 流式对话；
 * 1. 与 test_completions 测试类相比，只是设置 isCompatible = false 这样就是使用了新的数据结构。onEvent 处理接收数据有差异
 * 2. 仅支持 GLM_3_TURBO、GLM_4(新版模型) 使用其他模型会有解析错误
 */
@Test
public void test_completions_new() throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 添加参数：模型、请求信息
    ChatRequest request = new ChatRequest();
    request.setModel(Model.GLM_3_TURBO); // GLM_3_TURBO、GLM_4
    request.setIsCompatible(false);
    // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
    request.setTools(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 6579109928102918L;

        {
            add(ChatRequest.Tool.builder()
                    .type(ChatRequest.Tool.Type.WEB_SEARCH)
                    .webSearch(ChatRequest.Tool.WebSearch.builder().enable(true).searchQuery("快速排序").build())
                    .build());
        }
    });
    request.setMessages(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 20093837222321232L;

        {
            add(ChatRequest.Prompt.builder()
                    .role(Role.USER.value())
                    .content("小傅哥的是谁")
                    .build());
        }
    });
    // 请求
    chatGLMSession.completions(request, new EventSourceListener() {
        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
            if ("[DONE]".equals(data)) {
                log.info("[输出结束] Tokens {}", JSONUtil.toJsonStr(data));
                return;
            }
            ChatResponse response = JSONUtil.toBean(data, ChatResponse.class);
            log.info("测试结果：{}", JSONUtil.toJsonStr(response));
        }
        @Override
        public void onClosed(@NotNull EventSource eventSource) {
            log.info("对话完成");
            countDownLatch.countDown();
        }
        @Override
        public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
            log.error("对话失败", t);
            countDownLatch.countDown();
        }
    });
    // 等待
    countDownLatch.await();
}
```

> 兼容旧版

```java
/**
 * 流式对话；
 * 1. 默认 isCompatible = true 会兼容新旧版数据格式
 * 2. GLM_3_TURBO、GLM_4 支持联网等插件
 */
@Test
public void testCompletions() throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 传入参数：模型、请求信息
    ChatRequest request = new ChatRequest();
    request.setModel(Model.GLM_3_TURBO); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
    request.setIncremental(false);
    request.setIsCompatible(true); // 是否对返回结果数据做兼容，24年1月发布的 GLM_3_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异。开启 true 可以做兼容。
    // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
    request.setTools(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 28918019281211L;

        {
            add(ChatRequest.Tool.builder()
                    .type(ChatRequest.Tool.Type.WEB_SEARCH)
                    .webSearch(ChatRequest.Tool.WebSearch.builder().enable(true).searchQuery("小傅哥").build())
                    .build());
        }
    });
    request.setPrompt(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 921092817762812L;

        {
            add(ChatRequest.Prompt.builder()
                    .role(Role.USER.value())
                    .content("小傅哥的是谁")
                    .build());
        }
    });
    // 请求
    chatGLMSession.completions(request, new EventSourceListener() {
        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
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
    countDownLatch.await();
}
```

> 多模态图片识别(glm-4v模型的功能)

```java
@Test
public void test_completions_4v() throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 传入参数：模型、请求信息
    ChatRequest request = new ChatRequest();
    request.setModel(Model.GLM_4V); // GLM_3_TURBO、GLM_4
    request.setStream(true);
    request.setMessages(new ArrayList<>() {
        @Serial
        private static final long serialVersionUID = 728281918221L;

        {
            // content 字符串格式
            add(ChatRequest.Prompt.builder()
                    .role(Role.USER.value())
                    .content("这个图片有什么内容")
                    .build());
            // content 对象格式
            add(ChatRequest.Prompt.builder()
                    .role(Role.USER.value())
                    .content(ChatRequest.Prompt.Content.builder()
                            .type(ChatRequest.Prompt.Content.Type.TEXT.getCode())
                            .text("这是什么图片")
                            .build())
                    .build());
            // content 对象格式，上传图片；图片支持url、basde64
            add(ChatRequest.Prompt.builder()
                    .role(Role.USER.value())
                    .content(ChatRequest.Prompt.Content.builder()
                            .type(ChatRequest.Prompt.Content.Type.IMAGE_URL.getCode())
                            .imageUrl(ChatRequest.Prompt.Content.ImageUrl.builder().url("IMAGE_URL").build())
                            .build())
                    .build());
        }
    });
    chatGLMSession.completions(request, new EventSourceListener() {
        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
            if ("[DONE]".equals(data)) {
                log.info("[输出结束] Tokens {}", JSONUtil.toJsonStr(data));
                return;
            }
            ChatResponse response = JSONUtil.toBean(data, ChatResponse.class);
            log.info("测试结果：{}", JSONUtil.toJsonStr(response));
        }
        @Override
        public void onClosed(@NotNull EventSource eventSource) {
            log.info("对话完成");
            countDownLatch.countDown();
        }
        @Override
        public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
            log.error("对话失败", t);
            countDownLatch.countDown();
        }
    });
    // 等待
    countDownLatch.await();
}
```

#### 文字生成图片

ChatGPT要 4.0 才能使用的文生图功能，ChatGLM则免费开放，虽然画的技术可能比不上 ChatGPT 4.0，但是已经很不错了👍👍

```java
@Test
public void testImage(){
    ImageRequest request = new ImageRequest();
    request.setModel(Model.COGVIEW_3);
    request.setPrompt("画只可爱的中华田园犬");
    ImageResponse response = chatGLMSession.covertImages(request);
    System.out.println(JSONUtil.toJsonStr(response));
}
```
