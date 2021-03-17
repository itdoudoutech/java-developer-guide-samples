## 目录

+ [0x01：JNDI 获取数据源，自研 mvc 框架实现用户注册](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x01) 
+ [0x02：实现依赖注入和依赖查找，对密码和手机号进行格式校验](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x02)
+ [0x03：整合 Jolokia，扩展 microprofile config 的实现](https://github.com/coderlmm/java-developer-guide-samples/tree/main/user-platform#0x03)

## 0x01
### 目标

+ 通过自研 Web MVC 框架实现（可以自己实现）一个用户注册，forward 到一个成功的页面（JSP 用法）/register
+ 通过 Controller -> Service -> Repository 实现（数据库实现）
+ JNDI 的方式获取数据库源（DataSource），然后获取 Connection

### 实现

+ 用户注册

`com.doudou.user.web.controller.RegisterViewController` 为注册页面跳转 servlet，通过 `/register` 跳转至 `register.jsp`。
`com.doudou.user.web.controller.RegisterSubmitController` 为注册j接口所在 servlet，通过 `/submit` 来提交注册信息（用户名 & 密码）

+ 数据源实现

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

## 0x02
### 目标

+ 通过依赖注入和依赖查找，完善 `0x01` 的用户注册功能
+ 通过 UserService 实现用户注册注册用户需要校验
  + Id：必须大于 0 的整数
  + 密码：6-32 位
  + 电话号码: 采用中国大陆方式（11 位校验）

### 实现

+ 依赖注入 & 依赖查找

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

好比如一般情况下 controller 作为顶层组件是不会被其他组件依赖的，但是 controller 会依赖 service，所以 controller 也许要声明才行，不染 service 没有办法自动注入到 controller 中。

+ 数据校验

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

## 0x03
### 目标

+ 整合 [jolokia](https://jolokia.org/)，利用 Jolokia 通过 Servlet 代理自定义 JMX MBean
+ 完成 `org.eclipse.microprofile.config.Config` 中 API 的实现
    + 扩展 `org.eclipse.microprofile.config.spi.ConfigSource` 实现，包括 OS 环境变量，以及本地配置文件
    + 扩展 `org.eclipse.microprofile.config.spi.Converter` 实现，提供 String 类型到简单类型的转换
    + 通过 `org.eclipse.microprofile.config.Config` 读取当前应用名称
    + 应用名称 property name = “application.name”

### 实现

+ 整合 [jolokia](https://jolokia.org/)，利用 Jolokia 通过 Servlet 代理自定义 JMX MBean

引入 Manen 依赖：

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

+ 完成 Microprofile Config API 的实现

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
com.doudou.configuration.microprofile.config.converter.JavaStringConverter
com.doudou.configuration.microprofile.config.converter.JavaByteConverter
com.doudou.configuration.microprofile.config.converter.JavaShortConverter
com.doudou.configuration.microprofile.config.converter.JavaCharacterConverter
com.doudou.configuration.microprofile.config.converter.JavaIntegerConverter
com.doudou.configuration.microprofile.config.converter.JavaLongConverter
com.doudou.configuration.microprofile.config.converter.JavaFloatConverter
com.doudou.configuration.microprofile.config.converter.JavaDoubleConverter
com.doudou.configuration.microprofile.config.converter.JavaBooleanConverter
```

读取配置：

可在 `TestingListener#contextInitialized` 中验证。

```java
ConfigProviderResolver provider = ConfigProviderResolver.instance();
Config config = provider.getConfig();
String applicationName = config.getValue("application.name", String.class);

logger.info(String.format("converter string value [application.name = %s]", applicationName));
```