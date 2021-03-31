## 目录

+ [0x00：自研 MVC 框架实现用户注册，以 JNDI 方式获取数据源](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x00) 
+ [0x01：实现依赖注入和依赖查找，对密码和手机号进行格式校验](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x01)
+ [0x02：整合 Jolokia，扩展 microprofile config 的实现](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x02)
+ [0x03：完善 DI & configuration 模块，使其脱离 web.xml](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x03)
+ [0x04：完善 rest-client & reactive-messaging 模块](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x04)

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

+ [ ] 完善 rest-client 模块
+ [ ] 完善 reactive-messaging 模块

### 实现

+ [ ] 完善 rest-client 模块
  
  TODO...
  
+ [ ] 完善 reactive-messaging 模块
  
  TODO...


