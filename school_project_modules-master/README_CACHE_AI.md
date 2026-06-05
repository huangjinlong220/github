# 缓存数据与AI集成

> 📝 本教程介绍 Redis 缓存技术和 AI 大语言模型的集成

## 业务场景

项目中需要解决两个核心问题：
1. **缓存问题**：书籍详情查询频繁，数据库压力过大
2. **AI问题**：需要集成 AI 大语言模型提升用户体验

---

## 一、Redis缓存与Spring Cache

### 1.1 为什么需要缓存？

想象一下：如果图书馆管理系统每天有 1000 人查询同一本书籍详情，每次查询都要访问数据库，数据库压力会非常大。缓存就像图书馆的复印机——第一次查到书后复印一份，下次有人要查直接拿复印件，不用再去书架翻找。

**传统方式 vs 缓存方式**：

| 方式 | 优点 | 缺点 |
|-----|------|------|
| 每次查数据库 | 数据最新 | 压力大，响应慢 |
| 使用缓存 | 响应快，减轻数据库压力 | 数据可能过期 |

### 1.2 引入依赖

在 `school_comment` 模块中添加缓存依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 1.3 双Redis库配置

项目中需要区分不同的缓存用途：
- **Sa-Token**：存储登录Token，用 db=1
- **业务缓存**：存储书籍数据，用 db=2

**配置文件**（`cache-config.yml`）：

```yaml
springcache:
  redis:
    host: 127.0.0.1
    port: 6379
    database: 2
    timeout: 10s
```

### 1.4 RedisConfig 配置

创建双库Redis配置：

```java
@Configuration
@EnableCaching
public class RedisConfig {

    // ---------- Sa-Token Redis (db=1) ----------
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisProperties saTokenRedisProperties() {
        return new RedisProperties();
    }

    @Bean
    @Primary
    public RedisConnectionFactory saTokenRedisConnectionFactory(
            @Qualifier("saTokenRedisProperties") RedisProperties properties) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
            properties.getHost(), properties.getPort());
        factory.setDatabase(properties.getDatabase());
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean("saTokenRedisTemplate")
    public RedisTemplate<String, Object> saTokenRedisTemplate(
            @Qualifier("saTokenRedisConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    // ---------- Spring Cache Redis (db=2) ----------
    @Bean
    @ConfigurationProperties(prefix = "springcache.redis")
    public RedisProperties springCacheRedisProperties() {
        return new RedisProperties();
    }

    @Bean
    public RedisConnectionFactory springCacheRedisConnectionFactory(
            @Qualifier("springCacheRedisProperties") RedisProperties properties) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
            properties.getHost(), properties.getPort());
        factory.setDatabase(properties.getDatabase());
        factory.afterPropertiesSet();
        return factory;
    }

    // ---------- Spring Cache Manager ----------
    @Bean
    @Primary
    public RedisCacheManager cacheManager(
            @Qualifier("springCacheRedisConnectionFactory") RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericFastJsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(5));
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
```

**关键点**：
- ✅ **@Primary**：指定默认使用的Bean
- ✅ **双库分离**：Token和业务数据互不干扰
- ✅ **5分钟缓存**：`.entryTtl(Duration.ofMinutes(5))`

### 1.5 缓存注解

Spring Cache 提供四个常用注解：

| 注解 | 作用 | 使用场景 |
|-----|------|---------|
| `@Cacheable` | 查询缓存 | 首次查询写入缓存，后续直接读取 |
| `@CachePut` | 更新缓存 | 修改数据后更新缓存 |
| `@CacheEvict` | 删除缓存 | 删除数据时清除缓存 |
| `@Caching` | 组合注解 | 同时使用多个缓存操作 |

**代码示例**（BookServiceImpl）：

```java
@ApiOperation(value = "根据ID查询书籍详情", notes = "查询书籍详情，同时将浏览数+1，imageUrl已拼接完整访问路径")
@GetMapping("/detail")
@Cacheable(value = "bookCache", key = "#id")
public SaResult getBookDetail(@ApiParam(value = "书籍ID", required = true) @RequestParam Long id) {
    return bookService.getBookDetail(id);
}
```

**为什么这样做？**
- ✅ 第一次查询：从数据库读取，并存入缓存
- ✅ 后续查询：直接从缓存读取，不用查数据库
- ✅ 5分钟过期：保证数据不会太旧

### 1.6 已有代码修改

由于配置了双RedisTemplate，需要修改原来使用RedisTemplate的类：

```java
// EmailCodeRedisUtil.java
@Autowired
@Qualifier("saTokenRedisTemplate")  // 指定使用 db=1
private RedisTemplate<String, Object> redisTemplate;
```

---

## 二、集成AI大语言模型

### 2.1 为什么需要AI？

传统系统需要用户自己搜索查找书籍，AI可以：
- ✅ 智能推荐书籍
- ✅ 回答用户问题
- ✅ 个性化服务

### 2.2 升级JDK版本

langchain4j要求最低JDK17，修改父pom：

```xml
<properties>
    <java.version>17</java.version>
</properties>
```

### 2.3 创建school_ai模块

目录结构：
```
school_ai/
├── pom.xml
└── src/main/java/com/example/controller/ChatController.java
```

**pom.xml依赖**：

```xml
<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>school_comment</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-community-dashscope-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
</dependencies>
```

### 2.4 配置AI模型

**配置文件**（`langchain-config.yml`）：

```yaml
langchain4j:
  community:
    dashscope:
      chat-model:
        api-key: your-api-key-here
        model-name: qwen-plus
      streaming-chat-model:
        api-key: your-api-key-here
        model-name: qwq-32b
```

> **注意**：需要在[阿里百炼控制台](https://bailian.console.aliyun.com)获取API Key

### 2.5 普通聊天接口

**代码实现**（ChatController）：

```java
@Api(tags = "AI聊天")
@RestController
@RequestMapping("/book/ai")
public class ChatController {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @ApiOperation("AI聊天（普通模式）")
    @GetMapping("/chat")
    public SaResult chat(@ApiParam("用户消息") @RequestParam(defaultValue = "你是谁") String message) {
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(UserMessage.from(message))
                .build();
        ChatResponse response = chatLanguageModel.chat(chatRequest);
        return SaResult.ok().setData(response.content().text());
    }
}
```

**适用场景**：简单问答、书籍推荐

### 2.6 流式输出接口

**为什么需要流式输出？**
- ❌ 普通模式：等待AI全部生成完才返回，用户等待时间长
- ✅ 流式输出：一块一块返回，用户实时看到内容

**代码实现**：

```java
@ApiOperation("AI聊天（流式输出）")
@GetMapping(value = "/stream", produces = {"text/event-stream;charset=UTF-8", "text/plain;charset=UTF-8"})
public Flux<String> stream(@ApiParam("用户消息") @RequestParam(defaultValue = "你是谁") String message) {
    // 步骤 1：主线程校验登录
    StpUtil.checkLogin();
    
    // 步骤 2：返回流式Flux
    return Flux.create(fluxSink -> {
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(UserMessage.from(message))
                .build();

        streamingChatModel.chat(chatRequest, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                fluxSink.next(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                fluxSink.complete();
            }

            @Override
            public void onError(Throwable throwable) {
                fluxSink.error(throwable);
            }
        });
    });
}
```

**关键点**：
- ✅ **主线程校验登录**：流式返回使用异步线程，需要手动校验
- ✅ **使用WebFlux**：Flux是响应式流，支持流式输出

### 2.7 白名单配置

流式输出接口不需要登录验证，添加到白名单：

```yaml
whitelist:
  - /book/ai/stream
```

---

## 三、完整流程

### 3.1 缓存流程

```
用户请求查询 → 检查缓存 → 有数据直接返回
                    ↓ 无数据
              查询数据库 → 写入缓存 → 返回数据
```

### 3.2 AI聊天流程

```
用户发送消息 → 校验登录 → 调用AI模型 → 返回响应
                                            ↓
                                      流式返回（stream接口）
```

---

## 四、总结对比

### 4.1 Redis缓存

| 维度 | 传统方式 | 缓存方式 |
|-----|---------|---------|
| 查询速度 | 慢（每次查库） | 快（直接读缓存） |
| 数据库压力 | 大 | 小 |
| 数据实时性 | 最新 | 可能过期（5分钟） |

### 4.2 AI集成

| 维度 | 普通接口 | 流式接口 |
|-----|---------|---------|
| 响应时间 | 等待全部生成 | 实时返回 |
| 用户体验 | 等待时间长 | 即开即用 |
| 实现复杂度 | 简单 | 稍复杂 |

### 4.3 关键要点

1. ✅ **双Redis库**：Token和业务数据分离，互不干扰
2. ✅ **@Cacheable**：查询方法使用，自动缓存
3. ✅ **JDK17**：langchain4j要求最低JDK17
4. ✅ **主线程校验登录**：流式接口需要手动校验
5. ✅ **白名单**：排除AI流式接口的登录验证

**一句话总结**：
> 缓存就像图书馆的复印机，AI就像智能图书馆员——一个帮你快读查到书，一个帮你推荐书！🤖