## 目录

+ [0x00：自研 MVC 框架实现用户注册，以 JNDI 方式获取数据源](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x00) 
+ [0x01：实现依赖注入和依赖查找，对密码和手机号进行格式校验](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x01)
+ [0x02：整合 Jolokia，扩展 microprofile config 的实现](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x02)
+ [0x03：完善 DI & configuration 模块，使其脱离 web.xml](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x03)
+ [0x04：完善 rest-client & reactive-messaging 模块](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x04)
+ [0x05：完善 my-cache 模块](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x05)
+ [0x06：接入 Github/Gitee OAuth2 认证](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x06)
+ [0x07：解决多个 WebSecurityConfigurerAdapter 冲突问题](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x07)

## 0x00
### 目标

+ [x] 通过自研 Web MVC 框架实现（可以自己实现）一个用户注册，forward 到一个成功的页面（JSP 用法）/register
+ [x] 通过 Controller -> Service -> Repository 实现（数据库实现）
+ [x] JNDI 的方式获取数据库源（DataSource），然后获取 Connection

### 实现

+ [x] 用户注册

`com.doudou.user.web.controller.RegisterViewController` 为注册页面跳转 servlet，通过 `/register` 跳转至 `register.jsp`。
`com.doudou.user.web.controller.RegisterSubmitController` 为注册j接口所在 servlet，通过 `/submit` 来提交注册信息（用户名 & 密码）

+ [x] 数据源实现

`user-platform/user-web/src/main/webapp/META-INF/context.xml` 配置文件中添加如下配置：

```xml
<Resource name="jdbc/UserPlatformDB"
    type="javax.sql.DataSource"  auth="Container"
    description="h2 database for User Platform"
    maxActive="100" maxIdle="30" maxWait="10000"
    username="sa" password="sa"
    driverClassName="org.h2.Driver"
    url="jdbc:h2:mem:~/user-platform;DB_CLOSE_DELAY=-1"/>
```

之后利用 `javax.servlet.ServletContextListener` 的监听机制，在容器初始化完成之后的回调函数 `ServletContextListener#contextInitialized` 中通过 `Context#lookup` 来查找数据源，保存到程序中使用即可。

## 0x01
### 目标

+ [x]  通过依赖注入和依赖查找，完善 `0x01` 的用户注册功能
+ [x]  通过 UserService 实现用户注册注册用户需要校验
   + Id：必须大于 0 的整数
   + 密码：6-32 位
   + 电话号码: 采用中国大陆方式（11 位校验）

### 实现

+ [x] 依赖注入 & 依赖查找

首先所有的组件都需要在 `context.xml` 中进行声明。

```xml
<Resource name="bean/UserRepository" auth="Container"
    type="com.doudou.user.repository.DatabaseUserRepository"
    factory="org.apache.naming.factory.BeanFactory" />
```

依旧延续 `0x01` 的思路，在 `ServletContextListener#contextInitialized` 函数中处理，通过 `Context#lookup` 获取到 `java:comp/env` 目录下的所有 Resource。

有所改变的就是抽象出了组件上下文 `com.doudou.context.ComponentContext`，在完成所有的 Resource 的查找之后注入到 ComponentContext 中，最简单则可以使用一个 Map 来存储，对外暴露一个 `getComponent` 函数即可。 至此则完成了依赖查找。

至于依赖注入，则需要遍历获取到的所有 Resource 实例，通过反射获可以获取到 Resource 实例（我们把这个实例称之为 A）下所有标注 `@javax.annotation.Resource` 的属性，通过 `Resource#name` 则可以获取属性对应的组件名称（该组件名称也是一个 Resource 实例，我们称之为 B），最后依赖 `Context#lookup` 获取的具体的 Resource 实例 B 注入到 Resource 实例 A 中即可。

至此，完成了依赖注入和依赖查找。但是有一个问题就是，所有组件都需要在 `context.xml` 中进行声明，即使没有其他组件依赖自身，但只要自身依赖了其他组件，也需要声明。

好比如一般情况下 controller 作为顶层组件是不会被其他组件依赖的，但是 controller 会依赖 service，所以 controller 也需要声明才行，不然 service 没有办法自动注入到 controller 中。

+ [x] 数据校验

```java
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column
    @Size(min = 6, max = 32, message = "密码长度需为6-32位")
    private String password;

    @Column
    @NotNull
    // @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}", message = "手机号格式校验错误")
    @UserPhoneValid
    private String phoneNumber;
}
```

id 为自增主键盘，无需做过多校验。

password 只需要校验长度，使用 `@Size(min = 6, max = 32, message = "密码长度需为6-32位")` 即可，同时由于 JPA 是在 `EntityManager#persist` 时触发数据校验的，而密码入库时存储的是密文，因此需要在密码加密之前对原始密码做长度校验（见下文代码）。

`com.doudou.user.validator.bean.validation.UserPhoneValid` 自定义注解用于校验电话号码。同样也可使用注解 `javax.validation.constraints.Pattern` 配置正则表达式来校验。

```java
@Override
public boolean register(User user) {
    Set<ConstraintViolation<User>> violations = validator.validate(user);
    if (violations.size() > 0) {
        throw new RuntimeException(violations.iterator().next().getMessage());
    }
    String password = Md5Utils.encode(user.getPassword());
    user.setPassword(password);
    return userRepository.save(user);
}
```

## 0x02
### 目标

+ [x]  整合 [jolokia](https://jolokia.org/)，利用 Jolokia 通过 Servlet 代理自定义 JMX MBean
+ [x]  完成 `org.eclipse.microprofile.config.Config` 中 API 的实现
    + 扩展 `org.eclipse.microprofile.config.spi.ConfigSource` 实现，包括 OS 环境变量，以及本地配置文件
    + 扩展 `org.eclipse.microprofile.config.spi.Converter` 实现，提供 String 类型到简单类型的转换
+ [x]  通过 `org.eclipse.microprofile.config.Config` 读取当前应用名称
    + 应用名称 property name = “application.name”

### 实现

+ [x] 整合 [jolokia](https://jolokia.org/)，利用 Jolokia 通过 Servlet 代理自定义 JMX MBean

引入 Maven 依赖：

```xml
<dependency>
    <groupId>org.jolokia</groupId>
    <artifactId>jolokia-client-java</artifactId>
    <version>${jolokia.version}</version>
</dependency>
```

配置 web.xml：

```xml
<servlet>
    <servlet-name>jolokia-agent</servlet-name>
    <servlet-class>org.jolokia.http.AgentServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>jolokia-agent</servlet-name>
    <url-pattern>/jolokia/*</url-pattern>
</servlet-mapping>
```

定义 JMX MBean：

```java
public interface UserManagementMBean {

    void setName(String name);

    String getName();

    String toString();
}

public class UserManagement implements UserManagementMBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private String name;

    @Override
    public void setName(String name) {
        this.name = name;
        logger.info("UserManagement setName method invoke: " + name);
    }

    @Override
    public String getName() {
        logger.info("UserManagement getName method invoke: " + name);
        return name;
    }

    @Override
    public String toString() {
        logger.info("UserManagement print method invoke: " + name);
        return name;
    }
}
```


在 `com.doudou.user.web.listener.TestingListener#contextInitialized` 中注册 JMX MBean，完成。

```java
MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
mBeanServer.registerMBean(new UserManagement(), new ObjectName("com.doudou.user.jmx:type=UserManagement"));
logger.info("registerMBean success...");
```

验证：

GET 请求格式为：

```
<base-url>/read/<mbean name>/<attribute name>/<inner path>
<base url>/write/<mbean name>/<attribute name>/<value>/<inner path>
```

```json
$ curl http://localhost:8080/jolokia/read/com.doudou.user.jmx:type\=UserManagement | json

{
  "request": {
    "mbean": "com.doudou.user.jmx:type=UserManagement",
    "type": "read"
  },
  "value": {
    "Name": null
  },
  "timestamp": 1615970963,
  "status": 200
}
```

**注意上面的返回值，value 中的属性是 Name，而不是 name。所以我猜测名字取得是从 `getName()` 函数截取的。**

事实上，如果你请求读取 name 属性的话，会返回很详细的错误信息。

```json
curl http://localhost:8080/jolokia/read/com.doudou.user.jmx:type\=UserManagement/name

{
    "request": {
        "mbean": "com.doudou.user.jmx:type=UserManagement",
        "attribute": "name",
        "type": "read"
    },
    "stacktrace": "javax.management.AttributeNotFoundException: No such attribute: name......",
    "error_type": "javax.management.AttributeNotFoundException",
    "error": "javax.management.AttributeNotFoundException : No such attribute: name",
    "status": 404
}
```

```json
$ curl http://localhost:8080/jolokia/read/com.doudou.user.jmx:type\=UserManagement/Name | json

{
  "request": {
    "mbean": "com.doudou.user.jmx:type=UserManagement",
    "attribute": "Name",
    "type": "read"
  },
  "value": null,
  "timestamp": 1615970890,
  "status": 200
}

```

此时的 Name 还没有赋值，所以是 Null。

写数据：

```json
curl http://localhost:8080/jolokia/write/com.doudou.user.jmx:type\=UserManagement/Name/hello | json

{
  "request": {
    "mbean": "com.doudou.user.jmx:type=UserManagement",
    "attribute": "Name",
    "type": "write",
    "value": "hello"
  },
  "value": null,
  "timestamp": 1615972504,
  "status": 200
}
```

方法调用：

```json
curl http://localhost:8080/jolokia/exec/com.doudou.user.jmx:type\=UserManagement/toString | json

{
  "request": {
    "mbean": "com.doudou.user.jmx:type=UserManagement",
    "type": "exec",
    "operation": "toString"
  },
  "value": "hello",
  "timestamp": 1615974098,
  "status": 200
}
```

+ [x] 完成 Microprofile Config API 的实现

这个点就是要应用 Java 的 SPI 机制来进行扩展实现。

ConfigSource 扩展：

```
OS 环境变量：通过 System.getenv() 获取
com.doudou.configuration.microprofile.config.source.JavaOSPropertiesConfigSource 

Java 系统属性：通过 System.getProperties() 获取
com.doudou.configuration.microprofile.config.source.JavaSystemPropertiesConfigSource

本地配置文件：读取配置文件 META-INF/config.properties
com.doudou.configuration.microprofile.config.source.JavaLocalPropertiesConfigSource
```

Converter 扩展：

```
com.doudou.configuration.microprofile.config.converter.StringConverter
com.doudou.configuration.microprofile.config.converter.ByteConverter
com.doudou.configuration.microprofile.config.converter.ShortConverter
com.doudou.configuration.microprofile.config.converter.IntegerConverter
com.doudou.configuration.microprofile.config.converter.LongConverter
com.doudou.configuration.microprofile.config.converter.FloatConverter
com.doudou.configuration.microprofile.config.converter.DoubleConverter
com.doudou.configuration.microprofile.config.converter.BooleanConverter
```

读取配置：

可在 `TestingListener#contextInitialized` 中验证。

```java
ConfigProviderResolver provider = ConfigProviderResolver.instance();
ConfigBuilder builder = provider.getBuilder();
builder.addDefaultSources();
builder.addDiscoveredConverters();
Config config = builder.build();

String applicationName = config.getValue("application.name", String.class);
logger.info(String.format("converter string value [application.name = %s]", applicationName));
```

## 0x03
### 目标

+ [x] 完善 dependency-injection 模块
   + 脱离 web.xml 配置实现 ComponentContext 自动初始化
   + 使用独立模块并且能够在 user-web 中运行成功
+ [x] 完善 configuration 模块
   + Config 对象如何能被 mvc 使用
     + 如何在 ServletContext 中获取
     + 如何通过 ThreadLocal 获取

### 实现

+ [x] 完善 dependency-injection 模块

抽离 ComponentContext 到单独的 module「dependency-injection」中，通过 SPI 机制加载 ServletContainerInitializer 的实现类，在 onStartup 方法中初始化 ComponentContext 即可。

```java
public class WebApplicationServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        ComponentContext context = new ComponentContext();
        context.init(servletContext);
    }
}
```

+ [x] 完善 configuration 模块

抽离 microprofile.config 到单独的模块「configuration」中，同样是依赖 SPI 机制加载 ServletContainerInitializer 的实现类，
在 onStartup 中动态添加 ServletContextListener。

```java
public class ConfigSourcesInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        servletContext.addListener(ConfigSourceInitializeListener.class);
    }
}
```

```java
public class ConfigSourceInitializeListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver provider = ConfigProviderResolver.instance();
        ConfigBuilder builder = provider.getBuilder();
        // 同步 ClassLoader
        builder.forClassLoader(classLoader);
        // 配置 ConfigSource
        builder.addDefaultSources();
        builder.addDiscoveredSources();
        builder.withSources(new ServletContextConfigSource(servletContext));
        // 配置 Converter
        builder.addDiscoveredConverters();
        // 注册 Config
        provider.registerConfig(builder.build(), classLoader);
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
```

但是在 `ConfigSourceInitializeListener#contextInitialized` 中试图获取 classLoader 时出现了 `UnsupportedOperationException`

```java
java.lang.UnsupportedOperationException: Servlet 3.0规范的第4.4节不允许从未在web.xml，web-fragment.xml文件中定义或未用@WebListener注释的ServletContextListener调用此方法。
  at org.apache.catalina.core.StandardContext$NoPluggabilityServletContext.getClassLoader(StandardContext.java:7275)
  at com.doudou.context.ConfigSourceInitializeListener.contextInitialized(ConfigSourceInitializeListener.java:18)
```

查看 Servlet 3.0 规范，发现如下描述：

> If the ServletContext passed to the ServletContextListener’s contextInitialized method where the ServletContextListener was neither declared in web.xml or web-fragment.xml nor annotated with @WebListener then an UnsupportedOperationException MUST be thrown for all the methods defined in ServletContext for programmatic configuration of servlets, filters and listeners.

简言之就是如果一个 ServletContextListener 不是通过 web.xml 和 `@WebListener` 添加到 Servlet 容器的，那么在 `ServletContextListener#contextInitialized` 方法中调用 ServletContext 的任何犯方法都必须抛出 `UnsupportedOperationException`

解决方案：

在 DI 模块中自定义 WebApplicationContextListener 来代替 ServletContextListener

```java
/**
 * 替代 ServletContextListener
 */
public interface WebApplicationContextListener {

    /**
     * init
     *
     * @param sce ServletContextEvent
     */
    void contextInitialized(ServletContextEvent sce);

    /**
     * destroy
     *
     * @param sce ServletContextEvent
     */
    void contextDestroyed(ServletContextEvent sce);
}
```

定义 WebApplicationContextListenerInitializer 用于初始化所有的 WebApplicationContextListener，在该类中通过 SPI 获取到所有的自定义 Listener，之后通过其对应的注解 Priority 进行排序，数值越小优先级越高，最后通过 `WebApplicationContextListener#contextInitialized` 继续将事件分发下去。

```java
public class WebApplicationContextListenerInitializer implements ServletContextListener {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    private final static List<WebApplicationContextListener> contextListeners = new ArrayList<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServiceLoader<WebApplicationContextListener> webApplicationContextListeners
                = ServiceLoader.load(WebApplicationContextListener.class);
        webApplicationContextListeners.forEach(contextListeners::add);
        contextListeners.sort(WebApplicationContextListenerOrdinalComparator.INSTANCE);

        // 分发 WebApplicationContextListener
        for (WebApplicationContextListener contextListener : contextListeners) {
            contextListener.contextInitialized(sce);
            logger.info(contextListener.getClass().getSimpleName() + "Listener contextInitialized success");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (int i = contextListeners.size() - 1; i >= 0; i--) {
            contextListeners.get(i).contextDestroyed(sce);
        }
    }
}
```

WebApplicationContextListener 优先级比较器:

```java
public class WebApplicationContextListenerOrdinalComparator implements Comparator<WebApplicationContextListener> {

    public static final Comparator<WebApplicationContextListener> INSTANCE = new WebApplicationContextListenerOrdinalComparator();

    private WebApplicationContextListenerOrdinalComparator() {
    }

    @Override
    public int compare(WebApplicationContextListener o1, WebApplicationContextListener o2) {
        Priority priorityOne = o1.getClass().getAnnotation(Priority.class);
        Priority priorityTwo = o2.getClass().getAnnotation(Priority.class);
        int valueOne = null == priorityOne ? 0 : priorityOne.value();
        int valueTwo = null == priorityTwo ? 0 : priorityTwo.value();
        return Integer.compare(valueOne, valueTwo);
    }
}
```

如何使用：

```java
@Priority(value = 1)
public class ConfigSourceInitializeListener implements WebApplicationContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver provider = ConfigProviderResolver.instance();
        ConfigBuilder builder = provider.getBuilder();
        // 同步 ClassLoader
        builder.forClassLoader(classLoader);
        // 配置 ConfigSource
        builder.addDefaultSources();
        builder.addDiscoveredSources();
        builder.withSources(new ServletContextConfigSource(servletContext));
        // 配置 Converter
        builder.addDiscoveredConverters();
        // 注册 Config
        provider.registerConfig(builder.build(), classLoader);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
```

## 0x04
### 目标

+ [x] 完善 rest-client 模块
+ [x] 完善 reactive-messaging 模块

### 实现

+ [x] 完善 rest-client 模块

请求的逻辑需要在 `javax.ws.rs.client.Invocation` 中处理，而不同类型的请求处理逻辑可能会有些许不同，故抽象出 `com.doudou.rest.client.AbstractHttpInvocation` 类来处理公共逻辑部分，不同的逻辑则下放到具体子类中去实现。

```java
AbstractHttpInvocation
  - HttpGetInvocation
  - HttpPostInvocation
```

验证时需要在本地启动一个 rest 接口，方便起见，我直接写了一个最简单的 spring-boot 工程，可直接运行`/java-developer-guide-samples/spring-boot-samples/spring-boot-web-samples/src/main/java/com/doudou/Application`来启动 rest 接口。

spring-boot-web-samples 工程的 HelloWorldController:

```java
package com.doudou;

@RestController
@RequestMapping("/")
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/hi")
    public String hi(String name) {
        return "Hi," + name + "!";
    }
}
```

接着启动本工程下的 `com.doudou.rest.RestClientTest` 测试用例既可。

```java
package com.doudou.rest;

public class RestClientTest {

    @Test
    public void testGetMethod() {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response
        String content = response.readEntity(String.class);
        System.out.println(content);
    }

    @Test
    public void testPostMethod() {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hi")      // WebTarget
                .request() // Invocation.Builder
                .post(Entity.json("name=admin"));                                     //  Response
        String content = response.readEntity(String.class);
        System.out.println(content);
    }
}

## 输出如下：
信息: get method: Hello World
信息: post method: Hi,admin!
```

+ [x] 完善 reactive-messaging 模块
  

reactive-messaging 可以简单的理解为订阅发布模式。先了解下其主要的 API。

```java
package org.reactivestreams;

public interface Publisher<T> {
    // 添加订阅者
    void subscribe(Subscriber<? super T> s);
}

public interface Subscriber<T> {
    // 关联 Subscription
    void onSubscribe(Subscription s);
    
    // 接收 Publisher Event
    void onNext(T t);
    
    // 失败情况，Publisher 不再发送 Event
    void onError(Throwable t);
    
    // 成功处理完所有 Event，Publisher 不再发送 Event
    void onComplete();
}

public interface Subscription {

    /**
     * 发布通知前必须先调用此方法
     * @param n Subscriber 最多接受的 Event 个数
     */
    void request(long n);
    
    // 通知 Publisher 不再向 Subscriber 发送 Event
    void cancel();
}

public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {}
```

这几个 API 都不复杂，设计基本做到了见名知意，不在过多解释其含义，简单说下其设计思路。

Processor 集发布者和订阅者合二为一，目前暂未实现。

其中不好理解的是 Subscription，我们可以将其看作是 Publisher 和 Subscriber 的中间纽带，`Subscription#request` 控制着 Subscriber 最多能接收到的 Event 个数，`Subscription#cancel` 负责通知 Publisher 不再继续向 Subscriber 发送 Event。

注意，Subscription 和 Subscriber 是一一对应的，这点从 `Subscriber#onSubscribe` 可知。

因此，我们要在 Subscriber#onNext 函数中获取到该订阅者可接收的 Event 阈值，当达到阈值时调用自身 `onComplete` 函数，故 Subscription 实现类中需要暴露 maxRequestCount 和 isCancel 的接口.

Subscription 的实现如下：

```java
public class DefaultSubscription implements Subscription {

    private long maxRequest = -1;

    private boolean cancel = false;

    @Override
    public void request(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("The number of elements to requests must be more than zero!");
        }
        this.maxRequest = n;
    }

    @Override
    public void cancel() {
        this.cancel = true;
    }

    public long getMaxRequest() {
        return maxRequest;
    }

    public boolean isCancel() {
        return cancel;
    }
}
```

Subscriber 持有一个 Subscription 引用，继续将 maxRequestCount 和 isCancel 的接口暴露出去，具体实现则委托给 Subscription 即可。

Subscriber 的实现如下：

```java
public class DefaultSubscriber<T> implements Subscriber<T> {

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    private DefaultSubscription subscription;

    private boolean completed = false;

    private long requestCount = 0;

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = (DefaultSubscription) s;
    }

    @Override
    public void onNext(T t) {

        assertRequest();

        long maxRequestCount = subscription.getMaxRequest();
        if (requestCount == maxRequestCount && maxRequestCount < Long.MAX_VALUE) {
            onComplete();
            logger.warning(String.format("The number of requests is up to the max threshold[%d]," +
                    " the data subscription is completed!", maxRequestCount));
            return;
        }

        requestCount++;
        logger.info("收到数据：" + t);
    }


    @Override
    public void onError(Throwable t) {
        logger.info("遇到异常：" + t);
    }

    @Override
    public void onComplete() {
        completed = true;
        logger.info("收到数据完成");
    }

    public boolean isCanceled() {
        return subscription.isCancel();
    }

    public boolean isCompleted() {
        return completed;
    }

    private void assertRequest() {
        if (this.subscription.getMaxRequest() < 1) {
            throw new IllegalStateException("the number of request must be initialized before " +
                    "Subscriber#onNext(Object) method, please set the positive number to " +
                    "Subscription#request(int) method on Publisher#subscribe(Subscriber) phase.");
        }
    }
}
```

Publisher 则必须保存所有的 Subscriber，因此我们定义了一个 List，发布 Event 时则遍历该 List 以广播的形式通知出去即可，需要注意的是，广播时需要对 Subscriber 的状态做以下判定，canceled 和 completed 状态的的 Subscriber 不再继续发送 EVent。

Publisher 的实现如下：

```java
public class DefaultPublisher<T> implements Publisher<T> {

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    private final List<DefaultSubscriber<? super T>> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> s) {
        DefaultSubscriber<? super T> defaultSubscriber = (DefaultSubscriber) s;
        subscribers.add(defaultSubscriber);
    }

    public void publish(T data) {
        subscribers.forEach(p -> {
            if (p.isCompleted()) {
                logger.severe(String.format("The data subscription was completed, current data[%s] should not be published again!", data));
                return;
            }

            if (p.isCanceled()) { // Indicates that the Subscriber invokes Subscription#cancel() method.
                logger.warning(String.format("The Subscriber has canceled the data subscription," +
                        " current data[%s] will be ignored!", data));
                return;
            }

            p.onNext(data);
        });
    }
}
```

针对 Event 发布中被 cancel、completed、以及二次调用 `Subscription#request` 三种情况分别做测试。

测试程序如下：

```java
public class DefaultPublisherTest {

    private final DefaultPublisher<Integer> publisher = new DefaultPublisher<>();
    private final DefaultSubscription subscription = new DefaultSubscription();
    private final Subscriber<Integer> subscriber = new DefaultSubscriber<>();

    private final long maxRequestCount = 3L;

    ...
}
```

总共发布 4 次 Event，当发布第 3 个的时候，我们直接取消，看情况是否正常。

```java
@Test
public void testPublisherWithCancelCondition() {
    subscription.request(maxRequestCount);
    subscriber.onSubscribe(subscription);
    publisher.subscribe(subscriber);

    for (int i = 0; i < 4; i++) {
        if (i == 2) {
            subscription.cancel();
        }
        publisher.publish(i);
    }
}

## 日志如下：
信息: 收到数据：0
信息: 收到数据：1
警告: The Subscriber has canceled the data subscription, current data[2] will be ignored!
警告: The Subscriber has canceled the data subscription, current data[3] will be ignored!
```

总共发布 5 次 Event，但我们设置的 maxRequestCount 为 3，看情况是否正常。

```java
@Test
public void testPublisherWithCompleteCondition() {
    subscription.request(maxRequestCount);
    subscriber.onSubscribe(subscription);
    publisher.subscribe(subscriber);

    for (int i = 0; i < 5; i++) {
        publisher.publish(i);
    }
}

## 日志如下：
信息: 收到数据：0
信息: 收到数据：1
信息: 收到数据：2
信息: 收到数据完成
警告: The number of requests is up to the max threshold[3], the data subscription is completed!
严重: The data subscription was completed, current data[4] should not be published again!
```

总共发布 6 次 Event，因为设置的 maxRequestCount 为 3，所以在发布第 3 个 Event 之后将 maxRequestCount 重置为 100，看情况是否正常。

```java
@Test
public void testPublisherWithRequestAgainCondition() {
    subscription.request(maxRequestCount);
    subscriber.onSubscribe(subscription);
    publisher.subscribe(subscriber);

    for (int i = 0; i < 6; i++) {
        if (i == 4) {
            subscription.request(100L);
        }
        publisher.publish(i);
    }
}

## 日志如下：
信息: 收到数据：0
信息: 收到数据：1
信息: 收到数据：2
信息: 收到数据完成
警告: The number of requests is up to the max threshold[3], the data subscription is completed!
严重: The data subscription was completed, current data[4] should not be published again!
严重: The data subscription was completed, current data[5] should not be published again!
```
## 0x05
### 目标

+ [x] 完善 my-cache 模块
   + 提供一套抽象 API 实现对象的序列化和反序列化
   + 通过 Lettuce 实现一套 Redis CacheManager 以及 Cache

### 实现

+ 提供一套抽象 API 实现对象的序列化和反序列化

抽象出序列化 & 反序列化接口 `SerializeProvider`，并提供一个默认实现 `DefaultSerializeProvider`。

```java
/**
 * 序列化 & 反序列化
 *
 * @param <S> 原始数据类型
 * @param <T> 目标数据类型
 */
public interface SerializeProvider<S, T> {

    /**
     * 序列化
     *
     * @param source
     * @return
     */
    T serialize(S source) throws CacheException;

    /**
     * 反序列化
     *
     * @param target
     * @return
     */
    S deserialize(T target) throws CacheException;
}
```

```java
public class DefaultSerializeProvider implements SerializeProvider<Serializable, byte[]> {

    @Override
    public byte[] serialize(Serializable source) throws CacheException {
        byte[] bytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(source);
            objectOutputStream.flush();
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }

    @Override
    public Serializable deserialize(byte[] target) throws CacheException {
        if (target == null) {
            return null;
        }
        Serializable value;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(target);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            value = (Serializable) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return value;
    }
}
```

如何接入：

JedisCache 接入，哈哈哈，简单粗暴直接。

```java
private final SerializeProvider<Serializable, byte[]> serializeProvider;

    public JedisCache(CacheManager cacheManager, String cacheName,
                      Configuration<K, V> configuration, Jedis jedis) {
        super(cacheManager, cacheName, configuration);
        this.jedis = jedis;
        this.serializeProvider = new DefaultSerializeProvider();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serializeProvider.serialize(key);
        return jedis.exists(keyBytes);
    }

    ...
```

+ 通过 Lettuce 实现一套 Redis CacheManager 以及 Cache

Lettuce 通过 `io.lettuce.core.RedisClient` 来操作 Redis。

具体实现参考以下代码：

+ `com.doudou.cache.redis.lettuce.LettuceCacheManager`
+ `com.doudou.cache.redis.lettuce.LettuceCache`
+ `com.doudou.cache.redis.lettuce.DefaultRedisCodec`

#### TODO

自定义序列化反序列化 API 时发现写进 Redis 的 key 和 value 可读性是很差的。

![](https://raw.githubusercontent.com/coderlmm/java-developer-guide-samples/main/docs/img/redis-key-serializable.png)

我想是因为 `ByteArrayOutputStream#writeObject()` 在字节数组前面加了魔数，比如 String 类型的对象前面会加上 [-84, -19, 0, 5, 116, 0, 9]，事实上这个是不影响程序使用的，但是在 Redis 客户端查看时是极其不友好的。暂时未找到优雅的解决方案。


## 0x06
### 目标

+ [x] 使用 Spring Boot 来实现一个整合 Gitee 或者 Github OAuth2 认证

### 实现

+ 使用 Spring Boot 来实现一个整合 Gitee 或者 Github OAuth2 认证

本次作业实现并没有使用 Spring Boot，而是直接使用了之前的 user-web 和 configuration 模块对接 GitHub OAuth2 认证。

为了使 Controller 支持多方法映射，因此修改了 `FrontControllerServlet#service` 和 `FrontControllerServlet#initHandleMethods` 的实现，以支持单个 Controller 中的多方法映射。

OAuth2 认证流程图如下：

![](https://raw.githubusercontent.com/coderlmm/java-developer-guide-samples/main/docs/img/oauth_process.png)

主要逻辑代码实现如下：

```java
@Path("/")
public class OAuthApplicationController implements Controller {

    private final ConfigProviderResolver provider = ConfigProviderResolver.instance();
    private final Config providerConfig = provider.getConfig();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static final String GET_TOKEN_API = "https://github.com/login/oauth/access_token";
    private static final String GET_USER_INFO_API = " https://api.github.com/user";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRETS_KEY = "client_secrets";

    @GET
    @Path("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String client_id = providerConfig.getValue(CLIENT_ID_KEY, String.class);
        request.setAttribute("client_id", client_id);
        return "login-oauth.jsp";
    }

    @GET
    @Path("/oauth")
    public String oauth(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");

        // 获取 token
        Map<String, String> params = new HashMap<>();
        params.put("client_id", providerConfig.getValue(CLIENT_ID_KEY, String.class));
        params.put("client_secret", providerConfig.getValue(CLIENT_SECRETS_KEY, String.class));
        params.put("code", code);
        String result = OkHttpUtils.doPostWithParams(GET_TOKEN_API, params);
        logger.info("获取 GitHub token result: " + result);

        Map<String, String> resultMap = OkHttpUtils.resolveParamsByUrl("?" + result);
        String token = resultMap.get("access_token");

        // 获取个人信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "token " + token);
        String userInfo = OkHttpUtils.doGetWithHeaders(GET_USER_INFO_API, headers);
        logger.info("user info is: " + userInfo);

        JSONObject userObject = JSONObject.parseObject(userInfo);
        String name = userObject.getString("name");
        String avatar_url = userObject.getString("avatar_url");
        request.setAttribute("avatar_url", avatar_url);
        request.setAttribute("name", name);

        return "login-oauth-success.jsp";
    }
}
```

验证步骤：

安全起见，我将 `java-developer-guide-samples/user-platform/user-web/src/main/resources/META-INF/config.properties` 中的 client_id 以及 client_secrets 配置信息去掉了，因此你需要配置一下。

启动工程，访问 `http://localhost:8080/login` 页面，点击「click here」授权 GitHub 登录，之后就会看到如下页面了。

结果如下：

![](https://raw.githubusercontent.com/coderlmm/java-developer-guide-samples/main/docs/img/oauth_login_success.png)


## 0x07
### 目标

+ [x] 如何解决多个 WebSecurityConfigurerAdapter Bean 配置相互冲突的问题

提示：假设有两个 WebSecurityConfigurerAdapter Bean 定义，并且标注了不同的 @Order，其中一个关闭 CSRF，一个开启 CSRF，那么最终结果如何确定？

背景：Spring Boot 场景下，自动装配以及自定义 Starter 方式非常流行，部分开发人员掌握了 Spring Security 配置方法，并且自定义了自己的实现，解决了 Order 的问题，然而会出现不确定配置因素。

### 实现

TODO...




