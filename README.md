<p align="center">
  <img src=".idea/icon.svg" width="140" alt="mini-spring-boot">
</p>

# mini-spring-boot

[![build](https://github.com/Dlll-dash/mini-spring-boot/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/Dlll-dash/mini-spring-boot/actions/workflows/build.yml)

从零手写的 Spring 最小实现，希望帮助你更好理解spring，spring-boot原理。基于源码做简化实现：

| 模块 | 对标 | 负责 |
| --- | --- | --- |
| `mini-spring` | Spring Framework | IoC 容器、AOP、事件、条件装配、外部化配置、生命周期、MVC、JDBC 与事务 |
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
| `@Transactional`：进拦截器链，`begin` / `commit` / `rollback` | `transaction` 包 | spring-tx |
| 数据访问 | `JdbcTemplate` / `RowMapper` / `ResultSetExtractor` | spring-jdbc |
| `DataSource` 实现 | `DriverManagerDataSource`（JDK 只有 `javax.sql.DataSource` 接口，没有实现） | `DriverManagerDataSource` |
| 数据库连接的借与还 | `DataSourceUtils`：事务里有连接就复用，没有就新建；谁开的谁关 | `DataSourceUtils` |
| 线程绑定事务 | `TransactionSynchronizationManager`（`ThreadLocal<Map<DataSource, Connection>>`） | 同名类 |
| 事务管理器 | `DataSourceTransactionManager`：`begin` / `commit` / `rollback` / `cleanup` | `DataSourceTransactionManager` |
| 异常翻译 | `DataAccessException` / `EmptyResultDataAccessException` / `IncorrectResultSizeDataAccessException` | 同名家族 |
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
- MySQL 8（Demo测试需要，`Main` 从 ② 往后都走数据库，见「准备数据库」）
- 两个模块都有 `pom.xml`，根目录是父 pom（两个子模块通过 `<parent>` 继承），IDEA 可直接识别。
- 构建用仓库自带的 Maven Wrapper（`./mvnw`，Windows 是 `mvnw.cmd`），本机不需要装 Maven。
- 依赖：`jakarta.servlet-api`（provided，框架代码只在编译期用）、`tomcat-embed-core` + `tomcat-annotations-api`（内嵌容器）、`jackson-databind`（含 core / annotations，JSON）。IDEA / Maven 按 pom 解析；手写 `javac` 必须自己把它们放进 `-cp`。

## 构建与运行

 每个模块中都有对应的demo案例。

### 用 Maven 构建

```bash
./mvnw -B verify      # 编译 + 打包 + 跑测试；Windows 用 mvnw.cmd -B verify
./mvnw -B test        # 只跑测试
```

首次运行会把 Maven 3.9.12 下到 `~/.m2/wrapper`，之后直接用。
两个模块在同一个 reactor 里，`mini-spring-boot` 对 `mini-spring` 的依赖由 reactor 解析，不需要先 `install`。

测试放在 `mini-spring/src/test/java`，不连数据库、不起服务器。夹具在 `fixture/` 下按机制分包，
每个分组一个种子类（裸 `@ComponentScan`，只扫自己所在的包），互不干扰。

| 测试类 | 覆盖 |
| --- | --- |
| `MiniContextTest` | 扫描注册 / 单例 / 字段注入 / 循环依赖 |
| `ValueInjectionTest` | `@Value` 默认值与类型转换、未解析的占位符原样保留、`@PostConstruct` |
| `ConditionTest` | 假条件不进册子、`@ConditionalOnMissingBean` 从载体推导类型 |
| `BeanMethodTest` | `@Bean` 名字取自方法名、参数按类型注入 |
| `EventTest` | 第 12 步广播、按事件类型过滤 |
| `ProxyTransactionTest` | JDK 代理、begin/commit/rollback 顺序、字段里拿到的就是代理 |
| `web/PathMatcherTest` | 字面量段 / 槽位 / 段数 / 具体度 |
| `web/HandlerMappingTest` | 类级前缀、404、405、`@Controller` 闸门、路径变量 |
| `transaction/TransactionSynchronizationManagerTest` | 线程绑定、重复绑定抛异常、其它线程看不到 |
| `SimpleTypeConverterTest` | 类型转换、空串按 primitive 区分 |
| `StandardEnvironmentTest` | `${}` 解析的八种取值 |
| `OrderComparatorTest` | advisor 按 order 排序 |

### 准备数据库

`Main` 走真实数据库，先建库建表：

```sql
CREATE DATABASE mini_spring DEFAULT CHARSET utf8mb4;
USE mini_spring;
CREATE TABLE t_user (id BIGINT PRIMARY KEY, name VARCHAR(64));
INSERT INTO t_user (id, name) VALUES (42, 'bob');
```

`id = 42` 给查询用。`id = 12` 留给 `placeOrder`（写库后抛异常，用来验证回滚），不要预先插入。

三个连接参数用 `-D` 传：

```
-Dmini.db.url=jdbc:mysql://localhost:3306/mini_spring
-Dmini.db.user=root
-Dmini.db.password=<你的密码>
```

`-D` 是 VM options，不是环境变量。写成环境变量 `System.getProperty` 读不到，会报
`No suitable driver found for ${mini.db.url}`。IDEA 填 Run Configuration 的 VM options，
命令行写在 `java` 和主类之间。

### 手动编译（没有 Maven 时）

```bash
# 依赖 jar（版本见两个 pom.xml）：servlet-api、tomcat-embed-core、tomcat-annotations-api、
# jackson-databind / jackson-core / jackson-annotations。Windows 上分隔符是 ;
CP="<这些 jar 的路径>"

# 编译两个模块
# -parameters 不是可选项：@RequestParam 不写名字时靠它读形参名，
# 缺了它 /HelloController/hello 会直接 400。
javac -parameters -encoding UTF-8 -cp "$CP" -d target/manual \
      $(find mini-spring/src/main/java mini-spring-boot/src/main/java -name "*.java")

# ① IoC + AOP + 事件 + 配置 + JDBC + 事务
java -Dmini.db.url=jdbc:mysql://localhost:3306/mini_spring \
     -Dmini.db.user=root \
     -Dmini.db.password=<你的密码> \
     -cp "target/manual;mini-spring-boot/src/main/resources;$CP" \
     com.mini.spring.demo.Main

# ② 自动配置 + 内嵌 HTTP 容器（起在 8080，60 秒后自行关闭）
java -cp "target/manual;mini-spring-boot/src/main/resources;$CP" com.mini.boot.demo.BootDemo
```

`Main` 通过 `@Import` 引入了 `HandlerMapping`，它的方法签名里有 `HttpServletRequest`，
所以运行期 servlet-api 也得在 classpath 上（`$CP` 里已经包含）。
IDEA 里对应 Run Configuration → Modify options → Add dependencies with "Provided" scope to classpath。

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

`Main` 的输出（节选，需要上面三个 `-D` 参数）：

```
@PostConstruct>>OrderService
[event]：ContextRefreshedEvent ,listener@<hash>
skip com.mini.spring.ApplicationListener<com.mini.spring.demo.OrderPlacedEvent> for ContextRefreshedEvent
---①：配方名单---
[main, configurationClassPostProcessor, contextReadyListener, helloController, ...]
---②：测试循环依赖注入bean---
[tx] begin
[tx] commit
order of user#42
...
builder=[made by @Bean] greeting=[hello from $Proxy17]
Hello from @Component hits: 0
---⑦：数据库---
bob
```

最后一行 `placeOrder("tx-boom")` 故意先写库、后抛异常，用来验证回滚，所以进程以异常退出是预期的。
执行完 `t_user` 里不会有 `id = 12`。`$Proxy17` 是 JDK 动态代理的序号，每次运行可能不同。

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

两个模块各一个源码根。mini-spring 的分层对齐 Spring：**beans 造对象、context 装容器与事件、
context.annotation 做第 5 步、core 放与容器无关的通用件**，其余按能力分包。
每个文件后面一句话说清它管哪一段。

### mini-spring（框架层）

```
mini-spring/src/main/java/com/mini/spring/
│
│  入口
├── MiniContext.java                    容器：refresh 主干（第 5~12 步）、事件广播、close
│
│  造对象的一侧
├── beans/
│   ├── MiniBeanFactory.java            工厂：BeanDefinition + 三级缓存 + getBean / createBean
│   ├── BeanDefinition.java             配方：名字、类型、构造参数、工厂方法
│   ├── BeanDefinitionRegistry.java     注册表接口，由 BeanFactory 实现
│   ├── ObjectFactory.java              三级缓存里的函数接口
│   ├── BeanFactoryPostProcessor.java   改配方
│   ├── BeanPostProcessor.java          改成品
│   ├── Aware.java / BeanFactoryAware.java
│   └── InitAnnotationBeanPostProcessor.java   @Autowired / @PostConstruct / @Value
│
│  容器的一侧
├── context/
│   ├── ApplicationEvent.java / ApplicationListener.java / ApplicationEventPublisher.java
│   ├── ApplicationEventMulticaster.java / SimpleApplicationEventMulticaster.java
│   ├── ContextRefreshedEvent.java      第 12 步广播
│   ├── ContextClosedEvent.java         close() 里先广播
│   ├── Lifecycle.java                  start() / stop()：内嵌容器生命周期
│   └── annotation/                     第 5 步：从几颗种子推出全部配方
│       ├── ConfigurationClassProcessor.java   扫包 / @Import / @Bean / @Conditional
│       ├── Condition.java / OnMissingBeanCondition.java
│       └── ImportSelector.java / DeferredImportSelector.java / ImportBeanDefinitionRegistrar.java
│
│  与容器无关的通用件
├── core/
│   ├── Environment.java / StandardEnvironment.java / StringValueResolver.java
│   ├── SimpleTypeConverter.java        String → T，@Value 和 @RequestParam 共用
│   └── Ordered.java / OrderComparator.java
│
│  按能力分包
├── annotation/       @Component / @Autowired / @Value / @Bean / @Conditional / @Import ...
├── aop/              Pointcut / Advisor / AutoProxyCreator / MethodInterceptor / MethodInvocation
├── transaction/      @EnableTransactionManagement 及装配：点、面、拦截器、selector、registrar；
│                     PlatformTransactionManager 接口断开 jdbc 与 transaction 的包环
├── jdbc/
│   ├── core/         JdbcTemplate / RowMapper / ResultSetExtractor / PreparedStatementCallback
│   └── datasource/   DriverManagerDataSource / DataSourceUtils（借还连接）/ DataSourceTransactionManager
├── dao/              DataAccessException / EmptyResultDataAccessException / IncorrectResultSizeDataAccessException
├── exception/        NoSuchBeanDefinition / NoUniqueBeanDefinition / BeanAlreadyExists
├── web/              MVC：DispatcherServlet / HandlerMapping / HandlerAdapter / ArgumentResolver / WebUtils
└── demo/             可运行示例（不属于框架）：订单、用户、数据源配置、时间切面、事件监听、controller；
                      TransactionConfig 演示 @Import 的第三条分支（不带 @Component，只能走 @Import）
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

从哪个文件看起：`MiniContext`（主干）→ `beans/MiniBeanFactory`（造对象）→ `context/annotation/ConfigurationClassProcessor`（第 5 步）。

## 与 Spring 的差异

| 本实现 | Spring | 说明 |
| --- | --- | --- |
| 扫描时 `Class.forName` 加载类 | 只解析 `.class` 字节码，不加载类 | Spring 靠 ASM，本项目没有 |
| JDK 动态代理 | 默认 JDK 代理，无接口时用 CGLIB | 没有字节码增强，目标类没接口就进不了代理 |
| 内嵌 Tomcat（依赖 `tomcat-embed-core`） | 内嵌 Tomcat / Jetty / Undertow | 容器是第三方 jar，本实现负责造、开、关的接线 |
| 只有 `${}` 占位符 | 还有 `#{}` 表达式 | 没有 SpEL |
| 无 `@Configuration` / `proxyBeanMethods` | 有 | 需要字节码增强 |
| 按类型取 bean 撞车直接抛异常 | `@Primary` / `@Qualifier` / 参数名仲裁 | 只到"抛 `NoUniqueBeanDefinitionException`"为止 |
| 每次查询新建连接，没有连接池 | HikariCP / Tomcat JDBC Pool | 也没有 `@PreDestroy` 关池 |
| 事务只有"加入当前事务"一种行为，嵌套调用直接抛 | 7 种传播行为 | `bindResource` 发现已绑定就抛 `IllegalStateException` |
| 单例 + 同步事件广播 | 还有 prototype、`@Lazy`、异步、parent-child 容器 | 暂时没写 |

## 状态

已跑通：IoC 主干、AOP、事件、`@Bean`、`@Conditional`、`Environment` + `@Value`、
`@Import` 三条分支（外加 `DeferredImportSelector` 延迟导入）、自动配置清单、内嵌 Tomcat 容器、
JDBC（`DataSource` / `JdbcTemplate`）、声明式事务（线程绑定连接，回滚生效）、
MVC 主干（`@RestController`  + 类级 `@RequestMapping`  + 方法级 Mapping(`@GetMapping`、`PostMapping`...等) `@RequestParam`+`@PathVariable`+`@RequestBody`三种参数解析，解参失败 400 / 路径未映射 404 / 方法抛异常 500）。

构建：`./mvnw verify`。测试在 `mini-spring/src/test/java`，CI 见 `.github/workflows/build.yml`。

未实现：`@Qualifier` / `@Primary` 仲裁、prototype 作用域、`@PreDestroy` 与 Bean 销毁、
无接口类的代理、AspectJ 切点表达式、嵌套配置类、`@Bean("name")`、连接池、事务传播与嵌套事务。