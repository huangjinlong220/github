# school_project_modules 项目教程

## 项目介绍

图书管理系统分模块

> 一个一个创建项目的详细步骤此处省略，重点记录以下学习笔记：
> 1. 利用 Maven 分模块开发的学习笔记
> 2. 集成 Sa-Token 进行系统权限相关功能设计的学习笔记
> 3. 用户模块的实现学习笔记
> 4. 实现一种新的登录功能的学习笔记
> 5. 配置 Swagger 接口文档学习笔记
> 6. 全局异常处理学习笔记

---

## 一、Maven 分模块开发

### 1.1 创建父子工程（继承特性）

按照父子工程（Maven 实现的继承特性）的结构，创建了原始的 `school_project_modules` 父工程模块。该模块下只保留了全局的依赖包以及依赖包的版本管理。

### 1.2 按业务逻辑拆分模块

接下来按业务逻辑拆分为以下模块：

- **`school_admin`**：包含启动类及 `yml` 配置文件的模块
- **`school_comment`**：存放通用依赖的包（有别于父工程的版本管理，还提供统一返回、统一异常处理功能）
- **`school_security`**：进行系统权限校验功能的模块
- **`school_user`**：实现用户相关 CRUD 功能的模块

目录结构及功能分配如上，这便是分模块开发的第一步。

**图一：项目目录结构**

### 1.3 模块聚合与启动配置

启动 `school_admin` 模块时，如何将所有模块关联起来形成一个完整的项目？  
这里必须使用 Maven 的聚合特性：在父工程 `school_project_modules` 下添加四个子模块的依赖，从而完成聚合。

**图二：父工程添加的子模块依赖**

此外，需要注意数据库层的访问问题。需要在启动类中添加 `@MapperScan` 注解，指定数据库访问的包名。如果不加，启动类启动时会扫描当前包下的所有类，但数据库访问的包名未被扫描到，从而报错。

至此，一个完整的项目便搭建完成。

**图三：启动类添加的 MapperScan 注解**

---

## 二、集成 Sa-Token 实现权限控制

### 2.1 引入依赖

1. 在父工程中导入 Sa-Token 官方标准依赖，进行版本管理。
2. 在 `school_security` 模块中引入 Sa-Token 依赖（无需指定 `artifactId`）。
3. 在 `school_admin` 模块中添加核心配置 `satoken-config.yml`（使用官方配置即可）。

**图四：父工程添加的 Sa-Token 依赖**  
**图五：school_security 模块添加的 Sa-Token 依赖**

> **注意**：如果不想将 token 保存在默认的内存中，可替换为 Redis 实现分布式存储。此时需要重新导入 Redis 依赖包，并配置 Redis 连接（本项目已集成 Redis）。

### 2.2 登录流程

登录流程简述：根据用户名查询用户，校验密码是否正确。
- 验证失败 → 返回错误提示
- 验证成功 → 调用 Sa-Token 登录，并返回成功消息及令牌值

**图六：登录代码，那个方法**

### 2.3 跨域问题解决与全局拦截器配置

登录之后，还需解决跨域问题，配置全局拦截器 `SaTokenConfigure.java`。

```java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 它实现了 WebMvcConfigurer 接口
    // 这意味着它可以自定义 Spring MVC 的行为
}
```

#### 核心功能分解

**1️⃣ 配置 Token 序列化方式（`@PostConstruct` 方法）**

```java
@PostConstruct
public void rewriteComponent() {
    SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseBase64());
}
```

- **为什么这样做？**  
  Sa-Token 默认使用 Java 原生序列化，但不同版本的 JDK 可能存在兼容性问题。使用 Base64 序列化更加稳定，适合跨平台、跨版本场景。

**2️⃣ 设置全局登录验证（`addInterceptors` 方法）**

- 默认拦截所有请求（`"/**"`）
- 排除白名单路径（`notMatch(whitelist)`）
- 对被拦截的请求进行登录验证

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SaInterceptor(handler -> {
                SaRouter.match("/**", r -> {
                    List<String> whitelist = whitelistConfig.getWhitelist();
                    SaRouter.match("/**")
                            .notMatch(whitelist.toArray(new String[0]));
                });
            }))
            .addPathPatterns("/**");
}
```

**白名单示例**（`whitelist-config.yml` 从配置文件读取）：

```yaml
whitelist:
  - /login/**           # 登录接口（没登录怎么登录？）
  - /auth/**            # 认证相关接口
  - /swagger-ui/**      # API 文档（方便调试）
  - /favicon.ico        # 网站图标
```

**为什么需要这样配置？**

| 没有拦截器 | 有拦截器 |
|-----------|---------|
| ❌ 每个 Controller 都要手动检查登录 | ✅ 自动检查，无需重复代码 |
| ❌ 容易遗漏某个接口的验证 | ✅ 统一配置，安全可靠 |
| ❌ 代码混乱，难以维护 | ✅ 集中管理，清晰明了 |

**3️⃣ 配置跨域访问（`addCorsMappings` 方法）**

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

- **为什么需要跨域配置？**  
  假设前端运行在 `http://localhost:3000`，后端运行在 `http://localhost:8080`，浏览器会因安全机制阻止跨域请求。此时后端需要明确允许来自其他域的请求，即通过跨域配置声明“我信任来自其他域的请求”。

---

## 三、用户模块的实现

**业务场景**：用户模块是整个系统的基石，负责管理系统中所有用户的生命周期。从管理员创建账号，到用户登录使用系统，再到信息修改和账号注销，都离不开用户模块的 CRUD 操作。

**核心流程**：
1. 管理员创建用户（保存）
2. 用户信息变更（修改）
3. 账号禁用或注销（删除）
4. 查询用户信息（单个/列表/分页）
5. 密码重置（初始化密码）

---

### 3.1 实体类设计

**为什么需要 User 实体类？**
数据库中的 `sys_user` 表对应 Java 中的 User 实体类，就像银行的客户档案对应一个个真实的客户。没有实体类，我们就无法用 Java 代码操作数据库中的用户数据。

**代码实现**（`User.java`）：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
@ApiModel(description = "用户实体类")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("用户ID")
    private Long id;

    @NotNull(message = "名称不能为空")
    @ApiModelProperty("用户名称")
    private String name;

    @NotNull(message = "电话号码不能为空")
    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "手机号格式错误", groups = Valida.Create.class)
    @ApiModelProperty("电话号码")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 10, message = "密码长度需要6-10位")
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态")
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("submit_time")
    @ApiModelProperty("提交时间")
    private LocalDateTime submitTime;
}
```

**关键点**：
- ✅ `@TableName("sys_user")`：指定数据库表名
- ✅ `@TableId`：标记主键，`IdType.AUTO` 表示自增
- ✅ `@TableField`：映射非命名字段，如 `submit_time` 对应 `submitTime`
- ✅ `@Pattern`：手机号正则校验，只在 Create 分组生效
- ✅ `@Size`：密码长度校验，6-10 位

**代码位置**：`User.java` 第 22-61 行

---

### 3.2 保存用户

**业务场景**：管理员为新员工创建系统账号，就像去银行开户一样。

**流程图**：
```
管理员提交用户信息
    ↓
检查手机号是否已注册
    ↓
加密密码（BCrypt）
    ↓
设置创建时间
    ↓
保存到数据库
    ↓
返回结果
```

**详细步骤**：

1. **检查重复**：先查询数据库，看手机号是否已存在
2. **密码加密**：使用 BCrypt 加密，不可逆，保证安全
3. **设置时间**：记录创建时间，方便后续查询
4. **保存入库**：插入数据库

**代码实现**（`UserServiceImpl.java`）：

```java
@Override
public SaResult saveUser(User user) {
    // 步骤 1：检查手机号是否已注册
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(User::getPhone, user.getPhone());
    User existUser = this.getOne(wrapper);
    if (existUser != null) {
        return SaResult.error("该电话号码已注册");
    }
    
    // 步骤 2：加密密码
    String encryptedPwd = BCrypt.hashpw(user.getPassword());
    user.setPassword(encryptedPwd);
    // 步骤 3：设置创建时间
    user.setSubmitTime(LocalDateTime.now());
    
    // 步骤 4：保存到数据库
    boolean success = this.save(user);
    if (success) {
        return SaResult.ok("用户保存成功");
    }
    return SaResult.error("用户保存失败");
}
```

**为什么使用 BCrypt 加密？**
- ✅ **不可逆**：只能加密，不能解密，防止密码泄露
- ✅ **加盐**：自动生成随机盐值，防止彩虹表攻击
- ✅ **慢哈希**：计算耗时，暴力破解需要更长时间

**代码位置**：`UserServiceImpl.java` 第 22-40 行

---

### 3.3 修改用户

**业务场景**：用户的个人信息发生变化，需要更新。比如换手机号、修改备注等。

**流程图**：
```
提交修改后的用户信息
    ↓
检查用户是否存在
    ↓
判断是否修改了密码
    ↓
如果修改了密码，需要重新加密
    ↓
更新到数据库
    ↓
返回结果
```

**详细步骤**：

1. **检查存在**：先确认用户确实存在
2. **密码处理**：如果传了密码且与原密码不同，才重新加密
3. **更新入库**：使用 MyBatis-Plus 的更新方法

**代码实现**（`UserServiceImpl.java`）：

```java
@Override
public SaResult updateUser(User user) {
    // 步骤 1：检查用户ID是否为空
    if (user.getId() == null) {
        return SaResult.error("用户ID不能为空");
    }
    
    // 步骤 2：检查用户是否存在
    User existUser = this.getById(user.getId());
    if (existUser == null) {
        return SaResult.error("用户不存在");
    }
    
    // 步骤 3：如果修改了密码，需要重新加密
    if (user.getPassword() != null && !user.getPassword().equals(existUser.getPassword())) {
        String encryptedPwd = BCrypt.hashpw(user.getPassword());
        user.setPassword(encryptedPwd);
    }
    
    // 步骤 4：更新到数据库
    boolean success = this.updateById(user);
    if (success) {
        return SaResult.ok("用户修改成功");
    }
    return SaResult.error("用户修改失败");
}
```

**代码位置**：`UserServiceImpl.java` 第 42-63 行

---

### 3.4 删除用户

**业务场景**：员工离职或账号违规，需要删除用户账号。

**流程图**：
```
管理员提交删除请求
    ↓
检查用户是否存在
    ↓
从数据库删除
    ↓
返回结果
```

**代码实现**（`UserServiceImpl.java`）：

```java
@Override
public SaResult deleteUser(Long id) {
    // 步骤 1：检查用户ID是否为空
    if (id == null) {
        return SaResult.error("用户ID不能为空");
    }
    
    // 步骤 2：检查用户是否存在
    User existUser = this.getById(id);
    if (existUser == null) {
        return SaResult.error("用户不存在");
    }
    
    // 步骤 3：删除用户
    boolean success = this.removeById(id);
    if (success) {
        return SaResult.ok("用户删除成功");
    }
    return SaResult.error("用户删除失败");
}
```

**代码位置**：`UserServiceImpl.java` 第 65-81 行

---

### 3.5 查询用户

用户模块提供了三种查询方式：查询所有、根据 ID 查询、分页条件查询。

#### 3.5.1 查询所有用户

**代码实现**（`UserController.java`）：

```java
@ApiOperation("查询所有用户")
@GetMapping("/list")
public SaResult list() {
    List<User> list = userService.list();
    return SaResult.ok().setData(list);
}
```

**适用场景**：用户量较少的系统，如内部管理系统。

#### 3.5.2 根据 ID 查询用户

**代码实现**（`UserController.java`）：

```java
@ApiOperation("根据ID查询用户")
@GetMapping("/getById")
public SaResult getById(@ApiParam("用户ID") @RequestParam Long id) {
    User user = userService.getById(id);
    if (user != null) {
        return SaResult.ok().setData(user);
    }
    return SaResult.error("用户不存在");
}
```

**适用场景**：查看用户详情、编辑前获取用户信息。

#### 3.5.3 分页条件查询

**为什么需要分页查询？**
想象一下，如果系统有 10000 个用户，一次性查询所有数据会：
- ❌ 前端渲染卡顿
- ❌ 网络传输变慢
- ❌ 数据库压力巨大

分页查询就像看书一样，一页一页翻，既轻便又高效。

**流程图**：
```
前端请求（第1页，每页10条）
    ↓
后端接收参数（page=1, size=10）
    ↓
构建查询条件（姓名模糊匹配、时间范围）
    ↓
MyBatis-Plus 分页查询
    ↓
返回分页结果（含总数、当前页数据）
    ↓
前端渲染表格
```

**代码实现**（`UserServiceImpl.java`）：

```java
@Override
public SaResult selectPage(Integer page, Integer size, Map<String, Object> params) {
    // 步骤 1：创建分页对象
    Page<User> page1 = new Page<>(page, size);
    
    // 步骤 2：提取查询参数
    String name = params.get("name") != null ? params.get("name").toString() : null;
    String startTime = params.get("startTime") != null ? params.get("startTime").toString() : null;
    String endTime = params.get("endTime") != null ? params.get("endTime").toString() : null;
    
    // 步骤 3：构建查询条件
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(StringUtils.hasText(name), User::getName, name)
            .between(StringUtils.hasText(startTime) && StringUtils.hasText(endTime), 
                    User::getSubmitTime, startTime, endTime)
            .ge(StringUtils.hasText(startTime), User::getSubmitTime, startTime)
            .le(StringUtils.hasText(endTime), User::getSubmitTime, endTime);
    
    // 步骤 4：分页查询
    Page<User> userPage = this.page(page1, queryWrapper);
    return SaResult.data(userPage);
}
```

**代码位置**：`UserServiceImpl.java` 第 83-99 行

**关键逻辑**：
- ✅ **动态条件**：`StringUtils.hasText()` 判断参数是否存在，避免无效查询
- ✅ **模糊查询**：`.like()` 用于姓名搜索
- ✅ **范围查询**：`.between()`、`.ge()`、`.le()` 用于时间范围筛选

**Controller 接口**（`UserController.java`）：

```java
@ApiOperation("分页条件查询")
@ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "size", value = "每页大小", defaultValue = "10", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "name", value = "用户名", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "string", paramType = "query")
})
@GetMapping("/page")
public SaResult selectPage(@RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer size,
                           @RequestParam Map<String, Object> params) {
    return userService.selectPage(page, size, params);
}
```

**代码位置**：`UserController.java` 第 68-81 行

---

### 3.6 初始化密码

**业务场景**：用户忘记密码或管理员重置密码，将密码重置为默认密码 "123456"。

**为什么需要这个功能？**
- 用户忘记密码，自己无法找回（因为 BCrypt 不可逆）
- 管理员帮用户重置，无需知道原密码

**代码实现**（`UserServiceImpl.java`）：

```java
@Override
public SaResult initPassword(Long id) {
    // 步骤 1：检查用户ID
    if (id == null) {
        return SaResult.error("用户ID不能为空");
    }
    
    // 步骤 2：检查用户是否存在
    User user = this.getById(id);
    if (user == null) {
        return SaResult.error("用户不存在");
    }
    
    // 步骤 3：重置密码为 123456
    String encryptedPwd = BCrypt.hashpw("123456");
    user.setPassword(encryptedPwd);
    boolean success = this.updateById(user);
    
    if (success) {
        return SaResult.ok("密码初始化成功，默认为123456");
    }
    return SaResult.error("密码初始化失败");
}
```

**代码位置**：`UserServiceImpl.java` 第 101-120 行

---

### 3.7 Controller 接口汇总

**用户模块完整接口**（`UserController.java`）：

| 接口 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 查询所有 | GET | `/user/list` | 获取所有用户列表 |
| ID查询 | GET | `/user/getById` | 根据ID查询单个用户 |
| 保存用户 | POST | `/user/save` | 新增用户 |
| 修改用户 | POST | `/user/update` | 更新用户信息 |
| 删除用户 | GET | `/user/delete` | 删除用户 |
| 分页查询 | GET | `/user/page` | 分页+条件查询 |
| 初始化密码 | GET | `/user/initPassword` | 重置密码为123456 |

---

### 3.8 总结对比

**关键要点**：
1. ✅ **密码安全**：使用 BCrypt 加密，不可逆，保证安全性
2. ✅ **手机号唯一**：注册时检查手机号是否已注册
3. ✅ **动态查询**：分页查询支持动态条件，避免无效查询
4. ✅ **参数校验**：使用 `@Validated` 和分组校验，区分不同场景

**CRUD 操作对比**：

| 操作 | 关键点 | 注意事项 |
|-----|--------|---------|
| 保存 | 检查重复、密码加密 | 手机号不能重复 |
| 修改 | 判断是否修改密码 | 只加密修改了的密码 |
| 删除 | 检查存在性 | 确保用户存在再删除 |
| 查询 | 分页、动态条件 | 条件为空时不添加 |
| 重置 | 默认密码123456 | 直接加密覆盖 |

**一句话总结**：
> 用户模块就像公司的员工档案室——创建新员工、修改信息、查询档案、注销账号，一条龙服务！📁

---

## 四、实现一种新的登录功能

**业务场景**：实现一种新的登录方式——**邮箱验证码登录**，提升用户体验。

**传统登录 vs 邮箱验证码登录**：

| 登录方式 | 优点 | 缺点 |
|---------|------|------|
| 密码登录 | 用户熟悉 | 需要记住密码，容易忘记 |
| 邮箱验证码登录 | 无需记忆密码，安全可靠 | 需要等待邮件发送时间 |

**核心流程**：
1. 用户输入手机号码
2. 点击"发送验证码"按钮
3. 系统根据手机号查询绑定的 QQ 邮箱
4. 向该邮箱发送 6 位随机验证码
5. 用户填写验证码，完成登录

---

### 3.2 引入邮件依赖

1. 在父工程中导入 `spring-boot-starter-mail` 依赖。
2. 在 `school_comment` 模块中引入邮件依赖（无需指定版本）。
3. 在 `school_admin` 模块中添加邮件配置 `mail-config.yml`。

**图七：父工程添加的邮件依赖**  
**图八：mail-config.yml 配置文件**

> **注意**：需要在 QQ 邮箱设置中开启 SMTP 服务，获取授权码（不是登录密码）。

### 3.3 异步发送邮件

为了不阻塞主线程，使用异步方式发送邮件。需要配置线程池 `AsyncConfig.java`：

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 创建异步任务执行器
     * @return 线程池执行器
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：常驻线程数量
        executor.setCorePoolSize(5);
        // 最大线程数：队列满后创建的最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量：等待执行的任务队列大小
        executor.setQueueCapacity(100);
        // 线程空闲存活时间（秒）
        executor.setKeepAliveSeconds(60);
        // 线程名称前缀，便于日志追踪
        executor.setThreadNamePrefix("email-async-");
        // 拒绝策略：当队列和线程池都满时，由调用线程处理任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程池
        executor.initialize();
        return executor;
    }
}
```

- **为什么使用异步？**  
  邮件发送是 IO 密集型操作，可能需要几秒钟。如果使用同步方式，用户点击“发送验证码”后，页面会卡住直到邮件发送完成。使用异步可以让请求立即返回，提升用户体验。

### 3.4 验证码存储与 Redis 集成

验证码不能直接返回给用户，需要存储在服务器端进行校验。本项目使用 Redis 存储验证码：

**图九：验证码存储在 Redis 的**

**核心功能分解**

**1️⃣ 生成 6 位随机验证码**

```java
private String generateCode() {
    Random random = new Random();
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
}
```

**2️⃣ 发送中占位符（防止重复发送）**

**功能说明**：
- 用户点击发送后，先设置一个 30 秒的占位符
- 30 秒内再次请求，会提示"邮件正在发送中"
- 邮件发送成功后，将占位符替换为实际验证码

**为什么这样做？**  
如果不加限制，用户可以疯狂点击发送按钮，导致邮件轰炸。设置 30 秒冷却时间，可以有效防止恶意刷邮件。

**代码实现**：

```java
// 1. 创建发送中占位符（30 秒）
public void createSendingPlaceholder(String email) {
    String key = EMAIL_CODE_PREFIX + email;
    // 设置 30 秒的占位符，30 秒内无法再次请求发送验证码
    redisTemplate.opsForValue().set(key, SENDING_PLACEHOLDER, 30, TimeUnit.SECONDS);
}

// 2. 检查是否正在发送（判断是否为占位符状态）
public boolean isSendingPlaceholder(String email) {
    String key = EMAIL_CODE_PREFIX + email;
    Object value = redisTemplate.opsForValue().get(key);
    return SENDING_PLACEHOLDER.equals(value);
}

// 3. 将占位符更新为实际验证码
public void updatePlaceholderToCode(String email, String code) {
    String key = EMAIL_CODE_PREFIX + email;
    Object currentValue = redisTemplate.opsForValue().get(key);
    if (SENDING_PLACEHOLDER.equals(currentValue)) {
        // 如果当前是占位符，则更新为验证码，保持 5 分钟有效期
        redisTemplate.opsForValue().set(key, code, DEFAULT_CODE_EXPIRE, TimeUnit.MINUTES);
    } else {
        // 如果不是占位符，直接设置验证码（覆盖）
        redisTemplate.opsForValue().set(key, code, DEFAULT_CODE_EXPIRE, TimeUnit.MINUTES);
    }
}
```

**执行流程**：

```
用户请求发送 → createSendingPlaceholder() → Redis 设置 30 秒占位符
          ↓
   用户再次请求 → isSendingPlaceholder() → 返回 true → 拒绝请求
          ↓
   邮件发送成功 → updatePlaceholderToCode() → 占位符替换为验证码
          ↓
   30 秒后再次请求 → !isSendingPlaceholder() → 允许发送
```

---

**3️⃣ 验证码有效期管理**

| 类型 | 过期时间 | 说明 |
|-----|---------|------|
| 验证码 | 5 分钟 | 超过 5 分钟未验证，自动失效 |
| 错误尝试次数 | 30 分钟 | 记录验证失败次数，防止暴力破解 |

**代码实现**：

```java
// 1. 保存验证码（默认 5 分钟有效期）
public void saveEmailCode(String email, String code) {
    String key = EMAIL_CODE_PREFIX + email;
    redisTemplate.opsForValue().set(key, code, DEFAULT_CODE_EXPIRE, TimeUnit.MINUTES);
}

// 2. 获取验证码剩余过期时间（秒）
public Long getExpireTime(String email) {
    String key = EMAIL_CODE_PREFIX + email;
    if (isSendingPlaceholder(email)) {
        return -1L;  // 发送中状态
    }
    return redisTemplate.getExpire(key, TimeUnit.SECONDS);
}

// 3. 检查是否存在有效验证码（排除占位符状态）
public boolean hasValidCode(String email) {
    String key = EMAIL_CODE_PREFIX + email;
    Object value = redisTemplate.opsForValue().get(key);
    return value != null && !SENDING_PLACEHOLDER.equals(value);
}

// 4. 增加验证失败次数（防暴力破解）
public Long incrementAttemptCount(String email) {
    String key = EMAIL_ATTEMPT_PREFIX + email;
    Long count = redisTemplate.opsForValue().increment(key);
    if (count != null && count == 1) {
        // 第一次尝试，设置 30 分钟过期时间
        redisTemplate.expire(key, DEFAULT_ATTEMPT_EXPIRE, TimeUnit.MINUTES);
    }
    return count;
}

// 5. 获取验证失败次数
public Integer getAttemptCount(String email) {
    String key = EMAIL_ATTEMPT_PREFIX + email;
    Object count = redisTemplate.opsForValue().get(key);
    return count != null ? Integer.parseInt(count.toString()) : 0;
}

// 6. 验证成功后清空验证码和失败次数
public boolean validateEmailCode(String email, String inputCode) {
    String savedCode = getEmailCode(email);
    if (savedCode == null) {
        return false;  // 验证码不存在或已过期
    }
    if (savedCode.equals(inputCode)) {
        // 验证码正确，删除验证码并清空尝试次数
        deleteEmailCode(email);
        clearAttemptCount(email);
        return true;
    } else {
        // 验证码错误，增加尝试次数
        incrementAttemptCount(email);
        return false;
    }
}
```

**设计要点**：

1. **验证码 5 分钟过期**：使用 Redis 的 `set(key, value, timeout, unit)` 方法自动过期
2. **失败次数累加**：使用 `increment()` 原子操作递增计数
3. **首次失败设置过期**：只有第一次失败时才设置 30 分钟过期时间
4. **验证成功清空数据**：避免占用 Redis 内存空间

**4️⃣ 邮箱验证码登录完整流程**

### 📖 完整流程概览

**从注册到登录的完整生命周期**：

```
1️⃣ 注册开户 → 2️⃣ 发送验证码 → 3️⃣ 邮箱登录
```

**一句话总结**：
> **注册**就是办银行卡开户，**发送验证码**就是申请动态口令，**邮箱登录**就是凭动态口令进门——全程不需要记住密码，只需要手机号和邮箱！📧

---

### 🔹 流程一：注册开户（`SecurityController.register`）

**业务场景**：管理员为新员工创建系统账号

**流程图**：
```
用户提交注册信息
   ↓
后端检查用户名是否存在
   ↓
加密密码并保存用户信息
   ↓
返回注册成功
```

**详细步骤**：

1. **用户提交信息**：用户名、密码、手机号、邮箱
2. **检查重名**：根据用户名查询数据库，确保用户名唯一
3. **加密密码**：使用 BCrypt 对密码进行加密存储
4. **保存用户**：将用户信息存入数据库
5. **返回结果**：注册成功或失败

**代码实现**：

```java
@ApiOperation("用户注册")
@PostMapping("/register")
public SaResult register(@ApiParam("用户名") @RequestParam String username,
                         @ApiParam("密码") @RequestParam String password,
                         @ApiParam("手机号") @RequestParam String phone,
                         @ApiParam("邮箱") @RequestParam String email) {
    // 步骤 1：检查用户名是否已存在
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(User::getName, username);
    User existingUser = userMapper.selectOne(wrapper);
    
    if (existingUser != null) {
        return SaResult.error("用户名已存在");
    }

    // 步骤 2：创建新用户并加密密码
    User user = new User();
    user.setName(username);
    user.setPhone(phone);
    user.setPassword(BCrypt.hashpw(password)); // BCrypt 加密
    user.setEmail(email);
    user.setStatus("1"); // 默认启用状态
    
    // 步骤 3：保存到数据库
    int result = userMapper.insert(user);
    if (result > 0) {
        return SaResult.ok("注册成功");
    } else {
        return SaResult.error("注册失败");
    }
}
```

**代码位置**：`SecurityController.java` 第 27-56 行

**关键点**：
- ✅ 密码使用 BCrypt 加密，不可逆，保证安全性
- ✅ 注册时必须绑定邮箱，为后续邮箱登录做准备
- ✅ 用户名不能重复，避免账号冲突

---

### 🔹 流程二：发送验证码（`LoginEmailController.sendEmail`）

**业务场景**：用户申请邮箱验证码用于登录

**流程图**：
```
用户输入手机号
   ↓
后端查询用户及邮箱绑定情况
   ↓
检查验证码状态（已存在/发送中）
   ↓
生成验证码并发送邮件
   ↓
返回发送结果
```

**详细步骤**：

1. **接收手机号**：用户输入手机号码
2. **查询用户**：根据手机号查询用户信息
3. **检查邮箱**：确认用户已绑定邮箱
4. **验证码复用检查**：如果已有有效验证码，提示剩余有效期
5. **防重复发送**：检查是否正在发送中
6. **生成验证码**：创建 6 位随机验证码
7. **设置占位符**：防止 30 秒内重复发送
8. **异步发送邮件**：后台发送邮件，不阻塞主线程
9. **异常处理**：发送失败时清理占位符

**代码实现**：

```java
@ApiOperation("发送邮箱验证码")
@GetMapping("/sendEmail")
public SaResult sendEmail(@ApiParam("电话号码") @RequestParam String phone) {
    try {
        // 步骤 1：根据手机号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            return SaResult.error("没有该用户");
        }
        
        // 步骤 2：检查是否绑定邮箱
        if (user.getEmail() == null || "".equals(user.getEmail())) {
            return SaResult.error("该用户未绑定邮箱");
        }
        String email = user.getEmail();
        
        // 步骤 3：检查是否已有有效验证码
        if (emailCodeRedisUtil.hasValidCode(email)) {
            Long expireSeconds = emailCodeRedisUtil.getExpireTime(email);
            if (expireSeconds > 0) {
                return SaResult.ok("验证码已发送，请查收邮件。剩余有效期：" + 
                                   (expireSeconds / 60) + "分钟");
            }
        }
        
        // 步骤 4：检查是否正在发送中（防重复发送）
        if (emailCodeRedisUtil.isSendingPlaceholder(email)) {
            return SaResult.ok("邮件正在发送中，请稍后查看邮箱。若长时间未收到，请 2 分钟后重试");
        }
        
        // 步骤 5：生成验证码并设置占位符
        String code = generateCode();
        emailCodeRedisUtil.createSendingPlaceholder(email);
        
        // 步骤 6：异步发送邮件
        CompletableFuture<Boolean> future = 
            emailAsyncService.sendVerificationCodeAsync(email, "我来咯我来咯");
        future.whenComplete((result, throwable) -> {
            if (throwable != null || !result) {
                log.error("邮件发送失败，删除占位符：{}", email);
                emailCodeRedisUtil.deleteEmailCode(email);
            }
        });
        
        return SaResult.ok("验证码发送请求已接收，请 30 秒后查收邮件");
    } catch (Exception e) {
        e.printStackTrace();
        return SaResult.error("发送失败，请稍后重试");
    }
}
```

**代码位置**：`LoginEmailController.java` 第 37-74 行

**关键逻辑**：
- ✅ **验证码复用**：已有有效验证码且在有效期内，直接提示，避免重复发送
- ✅ **防刷机制**：30 秒占位符，防止恶意频繁请求
- ✅ **异步发送**：使用 `CompletableFuture` 不阻塞主线程，提升响应速度
- ✅ **容错处理**：发送失败自动清理占位符，允许用户重新请求

---

### 🔹 流程三：邮箱验证登录（`LoginEmailController.loginEmail`）

**业务场景**：用户使用邮箱验证码完成登录

**流程图**：
```
用户提交手机号 + 验证码
   ↓
后端查询用户信息
   ↓
从 Redis 提取验证码
   ↓
比对验证码
   ↓
正确：Sa-Token 登录 → 生成 Token → 返回成功
错误：提示验证码不正确
```

**详细步骤**：

1. **接收参数**：手机号和验证码
2. **查询用户**：根据手机号查找用户信息
3. **获取验证码**：从 Redis 中取出之前发送的验证码
4. **比对验证**：校验用户输入的验证码是否正确
5. **登录处理**：
    - 正确：调用 Sa-Token 登录，生成 Token，删除验证码
    - 错误：返回错误提示
6. **返回结果**：登录成功及 Token 信息

**代码实现**：

```java
@ApiOperation("邮箱登录")
@PostMapping("/email")
public SaResult loginEmail(@Validated(Valida.Create.class) 
                           @RequestBody LoginEmail loginEmail) {
    // 步骤 1：根据手机号查询用户
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(User::getPhone, loginEmail.getPhone());
    User user = userMapper.selectOne(queryWrapper);
    if (user == null) {
        return SaResult.error("没有该账号");
    }
    
    // 步骤 2：从 Redis 获取验证码
    String emailCode = emailCodeRedisUtil.getEmailCode(user.getEmail());
    if (!StringUtils.hasText(emailCode)) {
        return SaResult.error("该用户未发送验证码");
    }
    
    // 步骤 3：比对验证码
    if (loginEmail.getCode().equals(emailCode)) {
        // 步骤 4：验证码正确，执行登录
        StpUtil.login(loginEmail.getPhone());
        emailCodeRedisUtil.deleteEmailCode(user.getEmail());
        return SaResult.ok("登录成功").setData(StpUtil.getTokenInfo());
    } else {
        // 步骤 5：验证码错误
        return SaResult.error("验证码不正确");
    }
}
```

**代码位置**：`LoginEmailController.java` 第 76-96 行

**关键点**：
- ✅ **无需密码**：仅通过邮箱验证码完成身份验证
- ✅ **一次性使用**：验证成功后立即删除验证码，防止重用
- ✅ **自动过期**：验证码 5 分钟过期，超时自动失效
- ✅ **安全校验**：使用 `@Validated` 进行参数校验，确保数据完整性

**关键要点**：

1. **前提条件**：用户必须已注册并绑定了 QQ 邮箱
2. **无需密码**：通过邮箱验证码完成身份验证，提升用户体验
3. **安全可靠**：验证码 5 分钟过期，防止暴力破解
4. **异常处理**：未注册用户或未绑定邮箱时，提示联系管理员

---

### 💡 与传统密码登录对比

| 场景 | 密码登录 | 邮箱验证码登录 |
|-----|---------|--------------|
| **注册时** | 需要设置密码 | 只需绑定邮箱 |
| **登录时** | 输入手机号 + 密码 | 输入手机号 + 验证码 |
| **忘记密码** | 需要找管理员重置 | 不存在这个问题 |
| **安全性** | 密码可能被猜解 | 验证码 5 分钟过期，更安全 |
| **用户体验** | 需要记忆密码 | 无需记忆任何东西 |

---

## 五、配置 Swagger 接口文档

1. 在父工程中导入 `knife4j-openapi2-spring-boot-starter` 依赖，进行版本管理。
2. 在 `school_comment` 模块中引入依赖（无需指定 `artifactId`）。
3. 在 `school_admin` 模块中添加核心配置 `swagger-config.yml`。

**图九：父工程添加的 Knife4j 依赖**  
**图十：swagger-config.yml 配置文件**

### 4.2 实体类注解

在实体类上添加 Swagger 注解，用于生成接口文档：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
@ApiModel(description = "用户实体类")
public class User implements Serializable {
    
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("用户 ID")
    private Long id;

    @NotNull(message = "名称不能为空")
    @ApiModelProperty("用户名称")
    private String name;
    
    // ... 其他字段
}
```

- **`@ApiModel`**：描述整个实体类的作用
- **`@ApiModelProperty`**：描述每个字段的含义

### 4.3 Controller 接口注解

在 Controller 中添加注解，完善接口文档：

```java
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation("查询所有用户")
    @GetMapping("/list")
    public SaResult list() {
        List<User> list = userService.list();
        return SaResult.ok().setData(list);
    }

    @ApiOperation("根据 ID 查询用户")
    @GetMapping("/getById")
    public SaResult getById(@ApiParam("用户 ID") @RequestParam Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return SaResult.ok().setData(user);
        }
        return SaResult.error("用户不存在");
    }
}
```

**注解说明**：

| 注解 | 作用 | 示例 |
|-----|------|------|
| `@Api` | 描述 Controller 的功能 | `tags = "用户管理"` |
| `@ApiOperation` | 描述接口的作用 | `"查询所有用户"` |
| `@ApiParam` | 描述参数的含义 | `"用户 ID"` |

### 4.4 访问接口文档

启动项目后，访问以下地址即可查看接口文档：

```
http://localhost:8089/doc.html
```

**Knife4j 的优势**：

| 原生 Swagger UI | Knife4j |
|----------------|---------|
| ❌ 界面简陋 | ✅ 界面美观，支持深色模式 |
| ❌ 功能单一 | ✅ 支持离线文档导出、调试等功能 |
| ❌ 参数说明不清晰 | ✅ 参数表格化展示，一目了然 |

---

## 六、全局异常处理

如果没有全局异常处理，每个 Controller 方法都需要写 `try-catch`：

```java
// ❌ 没有全局异常处理的代码
@GetMapping("/user/{id}")
public SaResult getUser(@PathVariable Long id) {
    try {
        User user = userService.getById(id);
        if (user != null) {
            return SaResult.ok().setData(user);
        }
        return SaResult.error("用户不存在");
    } catch (Exception e) {
        log.error("查询用户失败", e);
        return SaResult.error("服务器内部错误");
    }
}
```

**问题**：
- 代码冗余：每个方法都要写重复的 `try-catch`
- 维护困难：修改异常处理逻辑时，需要改很多地方
- 影响阅读：业务逻辑被异常处理代码打断

### 5.2 使用 `@RestControllerAdvice` 统一处理

创建全局异常处理器 `GlobalExceptionHandler.java`：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public SaResult handlerNotLoginException(NotLoginException e) {
        String message;
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN: message = "未提供 Token"; break;
            case NotLoginException.INVALID_TOKEN: message = "Token 无效"; break;
            case NotLoginException.TOKEN_TIMEOUT: message = "Token 已过期"; break;
            case NotLoginException.BE_REPLACED: message = "账号已被顶下线"; break;
            case NotLoginException.KICK_OUT: message = "账号已被踢下线"; break;
            default: message = "当前会话未登录"; break;
        }
        return SaResult.error(message).setCode(401);
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SaResult handleValidException(MethodArgumentNotValidException e) {
        String errorMsg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return SaResult.error(errorMsg);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(value = Exception.class)
    public SaResult handleException(Exception e) {
        return SaResult.error("服务器内部错误" + e.getMessage()).setCode(500);
    }
}
```

### 5.3 异常处理流程

**1️⃣ 未登录异常（`NotLoginException`）**

当用户未登录或 Token 失效时，Sa-Token 会抛出此异常。根据不同的类型，返回不同的提示信息：

| 类型 | 说明 | 前端处理方式 |
|-----|------|-------------|
| NOT_TOKEN | 未提供 Token | 跳转到登录页 |
| INVALID_TOKEN | Token 无效 | 清除本地 Token，重新登录 |
| TOKEN_TIMEOUT | Token 已过期 | 刷新 Token 或重新登录 |
| BE_REPLACED | 账号被顶下线 | 提示用户，并跳转到登录页 |
| KICK_OUT | 账号被踢下线 | 禁止再次登录 |

**2️⃣ 参数校验异常（`MethodArgumentNotValidException`）**

当请求参数不符合 `@NotNull`、`@Pattern` 等校验规则时，会抛出此异常。直接从异常中提取校验失败的错误信息，返回给前端。

**示例**：

```java
@PostMapping("/register")
public SaResult register(@Validated(Valida.Create.class) @RequestBody User user) {
    // 如果 phone 字段不符合手机号格式，会自动抛出 MethodArgumentNotValidException
    // 前端会收到："手机号格式错误"
    return userService.saveUser(user);
}
```

**3️⃣ 其他所有异常（`Exception`）**

兜底处理，捕获所有未预料的异常，返回统一的错误提示。

### 5.4 配置校验分组

使用 `Valida` 接口定义不同的校验分组：

```java
public interface Valida {
    interface Login extends Default {}
    interface Create extends Default {}
    interface Update extends Default {}
    interface Delete extends Default {}
    interface Query extends Default {}
}
```

**为什么需要分组？**  
不同场景下，参数的校验规则可能不同。例如：
- **新增用户**：密码不能为空
- **更新用户**：密码可以为空（不修改密码）
- **删除用户**：只需要用户 ID

通过分组，可以灵活控制不同场景下的校验规则。

---

## 七、总结

至此，一个完整的图书管理系统后端框架已经搭建完成：

✅ **Maven 分模块开发**：按业务逻辑拆分为 admin、user、security、comment 四个模块  
✅ **Sa-Token 权限控制**：实现登录验证、Token 管理、全局拦截  
✅ **用户模块 CRUD**：完整的用户增删改查功能，支持分页查询、密码加密  
✅ **邮箱验证码登录**：异步发送邮件、Redis 存储验证码、防刷机制  
✅ **Swagger 接口文档**：Knife4j 美化界面、注解描述接口  
✅ **全局异常处理**：统一异常捕获、分类处理、友好提示

### 📊 技术栈汇总

| 技术 | 用途 | 核心优势 |
|-----|------|---------|
| **Spring Boot** | 基础框架 | 快速开发、自动配置 |
| **MyBatis-Plus** | ORM 框架 | 简化 SQL、分页查询 |
| **Sa-Token** | 权限认证 | 轻量级、功能全面 |
| **Redis** | 缓存中间件 | 高性能、支持过期策略 |
| **Knife4j** | 接口文档 | 界面美观、调试方便 |
| **BCrypt** | 密码加密 | 不可逆、加盐安全 |

### 🎯 核心功能回顾

**1. 用户管理全流程**
```
注册开户 → 保存用户 → 修改信息 → 删除账号 → 查询统计 → 密码重置
```

**2. 邮箱验证码登录流程**
```
输入手机号 → 发送验证码 → Redis 存储 → 校验验证码 → Sa-Token 登录 → 生成 Token
```

**3. 安全防护体系**
- ✅ BCrypt 密码加密（不可逆）
- ✅ 验证码 5 分钟过期（防暴力破解）
- ✅ 30 秒防刷机制（防恶意请求）
- ✅ 全局登录拦截（未登录自动拦截）
- ✅ 参数校验分组（不同场景不同规则）

### 💡 设计亮点

**1. 异步邮件发送**
- 使用线程池异步处理，不阻塞主线程
- 用户体验提升：点击发送立即响应

**2. Redis 智能管理**
- 占位符机制：防止重复发送
- 自动过期：验证码 5 分钟、错误次数 30 分钟
- 原子操作：increment() 累加失败次数

**3. 动态分页查询**
- 支持姓名模糊搜索
- 支持时间范围筛选
- 条件为空时自动忽略

**4. 统一异常处理**
- 分类处理：未登录、参数校验、其他异常
- 统一返回格式：SaResult
- 友好的错误提示

### 🚀 后续优化方向

项目中还有更多细节等待探索和完善：

- **日志记录**：使用 Logback 记录操作日志、异常日志
- **事务管理**：使用 `@Transactional` 保证数据一致性
- **Redis 缓存优化**：热点数据缓存、缓存穿透/雪崩解决方案
- **批量操作**：批量导入用户、批量删除
- **导出功能**：Excel 导出用户列表
- **文件上传**：用户头像上传、OSS 集成
- **定时任务**：定期清理过期验证码、数据统计
- **监控告警**：集成 Spring Boot Actuator、Prometheus

### 📖 学习收获

通过本项目的开发，你将掌握：

1. **Maven 多模块架构设计**：理解父子工程、依赖管理、模块聚合
2. **Spring Boot 整合能力**：整合 MyBatis-Plus、Redis、Sa-Token 等主流框架
3. **安全认证实践**：深入理解 Token 认证、权限拦截、加密算法
4. **异步编程思想**：线程池应用、CompletableFuture 异步任务
5. **API 设计规范**：RESTful 风格、Swagger 文档注解、统一返回格式
6. **异常处理最佳实践**：全局异常处理器、校验分组、友好提示

### 🎓 适用场景

本项目不仅是一个图书管理系统，更是一套**企业级开发脚手架**，可应用于：

- 📚 图书馆管理系统
- 🏫 学校教务管理系统
- 🏢 企业后台管理系统
- 🛒 电商平台后台
- 📊 数据统计分析系统

---

希望本教程能帮助你快速上手项目开发！🎉

**如果在开发过程中遇到问题，欢迎查阅以下资料：**
- [Sa-Token 官方文档](https://sa-token.cc/)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Knife4j 使用指南](https://xiaoym1019.github.io/knife4j/)