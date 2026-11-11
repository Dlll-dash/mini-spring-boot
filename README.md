<p align="center">
  <img src=".idea/icon.svg" width="140" alt="mini-spring-boot">
</p>

# mini-spring-boot

从零手写的 Spring 最小实现，希望帮助你更好理解spring，spring-boot原理。基于源码做简化实现：

| 模块 | 对标 | 负责 |
| --- | --- | --- |
| `mini-spring` | Spring Framework | IoC 容器、AOP、事件、条件装配、外部化配置、生命周期 |
| `mini-spring-boot` | Spring Boot | 自动配置清单、延迟导入、内嵌 Tomcat 容器 |



## 已实现的机制

### mini-spring（对标 Spring Framework）

| 机制 | 本实现 | 对应 Spring |
| --- | --- | --- |
| `refresh()` 主干 | `MiniContext` | `AbstractApplicationContext` |
| 包扫描 + BeanDefinition 注册 | `ConfigurationClassProcessor` | `ConfigurationClassPostProcessor` |
| Bean 创建、三级缓存、循环依赖 | `MiniBeanFactory` | `DefaultListableBeanFactory` + `DefaultSingletonBeanRegistry` |
| `@Autowired` 字段注入 / `@PostConstruct` / `@Value` | `InitAnnotationBeanPostProcessor` | `AutowiredAnnotationBeanPostProcessor` + `CommonAnnotationBeanPostProcessor` |
| 构造参数注入 | `MiniBeanFactory.createBean` | `ConstructorResolver` |
| `@Bean` 工厂方法 | 同上 | `ConfigurationClassBeanDefinitionReader` |
| `@Conditional` / `OnMissingBeanCondition` | `Condition` | `ConditionEvaluator` |
| `@Import` 三条通道 + 元注解递归 + `DeferredImportSelector` | `ConfigurationClassProcessor` | `ConfigurationClassParser` |
| AOP：Advisor / Pointcut / 拦截器链 / JDK 动态代理 | `aop` 包 | spring-aop |
| `@Transactional` | `transaction` 包 | spring-tx |
| 事件发布与类型过滤 | `ApplicationEvent` / `ApplicationListener` / `SimpleApplicationEventMulticaster` | spring-context 事件 |
| Advisor 排序 | `Ordered` / `OrderComparator` | `OrderComparator` |
| 容器生命周期 | `Lifecycle`（第 12 步 `start()`，`close()` 时 `stop()`） | `Lifecycle` + `DefaultLifecycleProcessor` |
| 外部化配置 | `StandardEnvironment` + `${}` 占位符解析 + 类型转换 | `Environment` + `PropertySourcesPlaceholderConfigurer` |
| MVC 主干 | `DispatcherServlet` / `HandlerMapping` / `HandlerAdapter` | `DispatcherServlet` / `RequestMappingHandlerMapping` / `RequestMappingHandlerAdapter` |
| 映射注解 | `@Controller` 闸门（元注解识别）+ 类级 `@RequestMapping` 前缀 + `@GetMapping` | `isHandler` + `RequestMappingInfo` |
| 参数解析 | `ArgumentResolver` 家族：`@RequestParam` / `@PathVariable`（名字回退需 `-parameters`）、`@RequestBody`（`HttpMessageConverter` 解 JSON） | `HandlerMethodArgumentResolver` 家族 |
| 返回值写出 | `@ResponseBody`（方法 / 类，含 `@RestController` 元注解）→ `HttpMessageConverter`；String 转换器在前、Jackson 在后 | `RequestResponseBodyMethodProcessor` / `AbstractMessageConverterMethodProcessor` |

### mini-spring-boot（对标 Spring Boot）

| 机制 | 本实现 | 对应 Spring Boot |
| --- | --- | --- |
| 自动配置清单加载 | `ImportCandidates`（读 `META-INF/spring/<注解全名>.imports`） | `ImportCandidates` |
| 延迟导入选择器 | `AutoConfigurationImportSelector` | `AutoConfigurationImportSelector` |
| 自动配置类 | `GreetingAutoConfiguration` | 各 `*AutoConfiguration` |
| 内嵌容器 | `MiniServletWebContext`（第 9 步造、第 12 步开、`close()` 停） | `ServletWebServerApplicationContext` + `TomcatWebServer` |
| MVC 接线 | `MiniServletWebContext` 注册 `handlerMapping` 配方，`/` 交给 `DispatcherServlet` | `DispatcherServletAutoConfiguration` + `WebMvcAutoConfiguration` |

## 环境要求

- JDK 21（`maven.compiler.release=21`）
- 两个模块都有 `pom.xml`，根目录是父 pom（两个子模块通过 `<parent>` 继承），IDEA 可直接识别；本地未安装 Maven，下面的命令用 `javac`。
- 依赖：`jakarta.servlet-api`（provided，只用于编译）、`tomcat-embed-core` + `tomcat-annotations-api`（内嵌容器）、`jackson-databind`（含 core / annotations，JSON）。IDEA / Maven 按 pom 解析；手写 `javac` 必须自己把它们放进 `-cp`。

## 构建与运行

 每个模块中都有对应的demo案例。

```bash
# 依赖 jar（版本见两个 pom.xml）：servlet-api、tomcat-embed-core、tomcat-annotations-api、
# jackson-databind / jackson-core / jackson-annotations。Windows 上分隔符是 ;
CP="<这些 jar 的路径>"

# 编译两个模块
# -parameters 不是可选项：@RequestParam 不写名字时靠它读形参名，
# 缺了它 /HelloController/hello 会直接 400。
javac -parameters -encoding UTF-8 -cp "$CP" -d target/manual \
      $(find mini-spring/src/main/java mini-spring-boot/src/main/java -name "*.java")

# ① IoC + AOP + 事件 + 配置
java -cp "target/manual;mini-spring-boot/src/main/resources;$CP" com.mini.spring.demo.Main

# ② 自动配置 + 内嵌 HTTP 容器（起在 8080，60 秒后自行关闭）
java -cp "target/manual;mini-spring-boot/src/main/resources;$CP" com.mini.boot.demo.BootDemo
```

两个模块的 `pom.xml` 也配了 `maven-compiler-plugin` 的 `<parameters>true</parameters>`（子模块通过 `<parent>` 继承），
走 Maven / IDEA 构建时不用再单独加。

Windows / PowerShell 下等价的一条：

```powershell
$CP = "<这些 jar 的路径，用 ; 分隔>"
javac -parameters -encoding UTF-8 -cp $CP -d target/manual (Get-ChildItem -Recurse -Filter *.java `
      mini-spring/src/main/java, mini-spring-boot/src/main/java | ForEach-Object FullName)
```

classpath 分隔符 Windows 是 `;`，Linux / macOS 是 `:`。
`mini-spring-boot` 的 `.imports` 清单在 `src/main/resources` 下，必须挂上，否则自动配置一个都不会加载。

`Main` 的输出（节选）：

```
---①：配方名单---
[main, configurationClassPostProcessor, contextReadyListener, defaultGreeter, ...]
---②：测试循环依赖注入bean---
[tx] begin
[tx] commit
order of user#42
...
builder=[made by @Bean] greeting=[hello from $Proxy17]
Hello from @Component hits: 0
```

`BootDemo` 起来之后，60 秒窗口内：

```bash
curl "http://localhost:8080/HelloController/hello?x=abc"
# Hello mini SpringBoot from abc          ← 200，x 来自 query

curl "http://localhost:8080/HelloController/add?a=1&b=2"
# 3                                       ← 200，String 转 int

curl "http://localhost:8080/HelloController/add?a=x&b=2"
# Cannot convert [x] to int               ← 400，请求方的错

curl "http://localhost:8080/HelloController/item/42"
# 42                                      ← 200，@PathVariable

curl -X POST "http://localhost:8080/HelloController/greet" -H "Content-Type: application/json" -d "{\"name\":\"abc\"}"
# {"name":"abc!"}                          ← 200，@RequestBody 解、@ResponseBody 写

curl "http://localhost:8080/nope"
# handler not found in path: /nope        ← 404，路径没映射
```

`/HelloController` 是类级 `@RequestMapping` 的前缀，`/hello` 是方法级的 `@GetMapping`。

## 目录结构

两个模块各一个源码根，分包只遵循一条：**根包放容器骨架**（它们是一台机器的零件，一个文件对一个类），
**子包放同类的东西**（注解、切面、事务、异常、示例）。每个文件后面一句话说清它管哪一段。

### mini-spring（框架层）

```
mini-spring/src/main/java/com/mini/spring/
│
│  ── 容器骨架：IoC ──
├── MiniContext.java                      容器：refresh 主干（第 5~12 步）、事件广播、close
├── MiniBeanFactory.java                  工厂：BeanDefinition + 三级缓存 + getBean / createBean
├── BeanDefinition.java                   配方：名字、类型、构造参数、工厂方法
├── BeanDefinitionRegistry.java           BeanDefinition 注册表接口，由BeanFactory实现
├── ObjectFactory.java                    三级缓存里的函数接口：lambda 重载，获取代理对象
├── InitAnnotationBeanPostProcessor.java  @Autowired / @PostConstruct / @Value 的实际执行者
│
│  ── 配置环境：@Value 注入环境变量 ──
├── Environment.java                      配置来源（接口）
├── StandardEnvironment.java              实现：System.getProperties
├── StringValueResolver.java              ${} 解析器，被 @Value 那条路调用
│
│  ── 扩展口：后置处理器 ──
├── BeanFactoryPostProcessor.java         修改BeanDefinition
├── BeanPostProcessor.java                修改Bean
├── Aware.java                            回调根接口
├── BeanFactoryAware.java                 实现类被创建的时候进行回调，注入BeanFactory
│
│  ── 配置解析：第 5 步扫包注册 ──
├── ConfigurationClassProcessor.java      扫包 / @Import / @Bean / @Conditional
├── Condition.java                        条件：判据由用户给，时机由容器定
├── OnMissingBeanCondition.java           @ConditionalOnMissingBean 的雏形
├── ImportSelector.java                   @Import 分支①：返回类名
├── DeferredImportSelector.java           分支①的延迟版（自动配置靠它）
├── ImportBeanDefinitionRegistrar.java    @Import 分支②：一个时机 + 一个空位
│
│  ── 事件：第 12 步广播 ──
├── ApplicationEvent.java                 事件基类
├── ApplicationEventPublisher.java        发布能力（MiniContext 实现它）
├── ApplicationListener.java              监听器
├── ApplicationEventMulticaster.java      多播器（接口）
├── SimpleApplicationEventMulticaster.java    按泛型过滤 + 广播
├── ContextRefreshedEvent.java            第 12 步广播
├── ContextClosedEvent.java               close() 里先广播
│
│  ── 顺序与生命周期：谁先谁后 ──
├── Ordered.java                          advisor 自报顺序
├── OrderComparator.java                  链的顺序在这里定
├── Lifecycle.java                        start() / stop()：管理内嵌容器生命周期
│
│  ── 子包 ──
├── annotation/       
├── aop/              Pointcut / Advisor / AutoProxyCreator / MethodInterceptor / MethodInvocation
├── transaction/      @EnableTransactionManagement 及装配：点、面、拦截器、selector、registrar、默认代理器
├── config/           TransactionConfig —— 普通配置类，演示 @Import 的第三条分支
├── exception/        NoSuchBeanDefinition / NoUniqueBeanDefinition / BeanAlreadyExists
├── web/              MVC：DispatcherServlet / HandlerMapping / HandlerAdapter / ArgumentResolver / WebUtils
└── demo/             可运行示例：订单、用户、时间切面、事件监听、controller、ProbeHandler（不属于框架）
```

### mini-spring-boot（Boot 层）

```
mini-spring-boot/src/main/java/com/mini/boot/
├── annotation/
│   └── AutoConfiguration.java            空注解，只当清单文件名用，没有行为
├── context/annotation/
│   ├── ImportCandidates.java             读 META-INF/spring/<注解全名>.imports
│   └── AutoConfigurationImportSelector.java  DeferredImportSelector：把清单里的类交出去
├── web/
│   ├── MiniServletWebContext.java        覆写 onRefresh：造服务器 + 注册 lifecycle bean + 注册 handlerMapping + 挂 DispatcherServlet
│   └── WebServerStartStopLifecycle.java  在refresh()最后一步 start()。
└── demo/
    ├── BootDemo.java                     入口：起服务 → 60 秒窗口 → close
    ├── ImportCandidatesDemo.java         
    ├── Controller/
    │   └── HelloController.java          @RequestMapping("/HelloController")
    ├── service/
    │   └── WelcomService.java            
    └── autoconfig/
        └── GreetingAutoConfiguration.java  demo 自动配置类

mini-spring-boot/src/main/resources/META-INF/spring/
└── com.mini.boot.annotation.AutoConfiguration.imports   ← 文件名 = 注解全名
```

从哪个文件看起：`MiniContext`（主干）→ `MiniBeanFactory`（造对象）→ `ConfigurationClassProcessor`（第 5 步）。

## 与 Spring 的差异

| 本实现 | Spring | 说明 |
| --- | --- | --- |
| 扫描时 `Class.forName` 加载类 | 只解析 `.class` 字节码，不加载类 | spring扫描不加载功能依赖于ASM，本项目暂时没有。 |
| JDK 动态代理 | 默认 JDK 代理，无接口时用 CGLIB | spring有CGLIB字节码加强，本项目没有 |
| `com.sun.net.httpserver.HttpServer` | 内嵌 Tomcat / Jetty | 本项目暂时还没写Tomcat                              |
| 只有 `${}` 占位符，无 `#{}`，支持#注解 | 两套 resolver | 没有 SpEL |
| 无 `@Configuration` / `proxyBeanMethods` | 有 | 需要字节码增强，本项目没有 |
| 按类型取 bean 撞车直接抛异常 | `@Primary` / `@Qualifier` / 参数名仲裁 | mini 只到"抛 `NoUniqueBeanDefinitionException`"为止 |
| 单例 + 事件只做了同步广播 | 还有 prototype、`@Lazy`、异步、parent-child 容器 | 暂时还没写 |

## 状态

已跑通：IoC 主干、AOP、事件、`@Bean`、`@Conditional`、`Environment` + `@Value`、
`@Import` 三条分支（外加 `DeferredImportSelector` 延迟导入）、自动配置清单、内嵌 Tomcat 容器、
MVC 主干（`@RestController` 闸门 + 类级 `@RequestMapping`  + `@RequestParam`+`@PathVariable`+`@ResquestBody`，解参失败 400 / 路径未映射 404 / 方法抛异常 500）。

未实现：`@Qualifier` / `@Primary` 仲裁、prototype 作用域、`@PreDestroy` 与 Bean 销毁、
无接口类的代理、AspectJ 切点表达式、嵌套配置类、`@Bean("name")`、。