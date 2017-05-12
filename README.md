# spring-data-redis-demo



**一、概念简介：**

**Redis：**

　　Redis是一款开源的Key-Value数据库，运行在内存中，由ANSI C编写，详细的信息在Redis官网上面有，因为我自己通过google等各种渠道去学习Redis，走了不少弯路，所以总结一条我认为不错的学习路径给大家：

　　1.《The Little Redis Book》

是一本开源PDF，只有29页的英文文档，看完后对Redis的基本概念应该差不多熟悉了，剩下的可以去Redis官网熟悉相关的命令。

　　2.《Redis设计与实现》

如果想继续深入，推荐这本书，现在已经出到第二版了，有纸质版书籍可以购买。上面详细介绍了Redis的一些设计理念，并且给出了一些内部实现方式，和数据结构的C语言定义，有一些基本C语言基础，就能看明白。

　　3.Redis 2.6源代码：

《Redis设计与实现》的作者发布在Github上的一个开源项目，有作者详细的注释。

 

**Jedis:**

　　Jedis是Redis官方推出的一款面向Java的客户端，提供了很多接口供Java语言调用。可以在Redis官网下载，当然还有一些开源爱好者提供的客户端，如Jredis、SRP等等，推荐使用Jedis。

 

**Spring Data Redis**

　　SDR是Spring官方推出，可以算是Spring框架集成Redis操作的一个子框架，封装了Redis的很多命令，可以很方便的使用Spring操作Redis数据库，Spring对很多工具都提供了类似的集成，如Spring Data MongDB…

　　这三个究竟有什么区别呢？可以简单的这么理解，Redis是用ANSI C写的一个基于内存的Key-Value数据库，而Jedis是Redis官方推出的面向Java的Client，提供了很多接口和方法，可以让Java操作使用Redis，而Spring Data Redis是对Jedis进行了封装，集成了Jedis的一些命令和方法，可以与Spring整合。在后面的配置文件（redis-context.xml）中可以看到，Spring是通过Jedis类来初始化connectionFactory的。

 

**二、Spring Data Redis Demo**

**项目目录：**

 

**![img](http://images.cnitblog.com/i/644226/201406/301656163245529.png)![img](http://images.cnitblog.com/i/644226/201406/301656236214291.png)**

 

**Pom.xml配置：****　**

　　Spring jar因为比较多，就不贴出来了，读者可以下载后面的项目源码查看详细配置，其实pom.xml可以精简，并非一定需要写的这么细，我之所以这么写，是为了看得更清楚。

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 <!-- config junit jar -->
 2         <dependency>
 3             <groupId>junit</groupId>
 4             <artifactId>junit</artifactId>
 5             <version>4.8.2</version>
 6             <scope>test</scope>
 7         </dependency>
 8         <!-- config redis data and client jar -->        
 9         <dependency>
10             <groupId>org.springframework.data</groupId>
11             <artifactId>spring-data-redis</artifactId>
12             <version>1.0.2.RELEASE</version>
13         </dependency>
14         <dependency>
15             <groupId>redis.clients</groupId>
16             <artifactId>jedis</artifactId>
17             <version>2.1.0</version>
18         </dependency>
19 
20         <!-- config need jar -->
21         <dependency>
22             <groupId>commons-lang</groupId>
23             <artifactId>commons-lang</artifactId>
24             <version>2.6</version>
25         </dependency>
26         <dependency>
27             <groupId>org.apache.geronimo.specs</groupId>
28             <artifactId>geronimo-servlet_3.0_spec</artifactId>
29             <version>1.0</version>
30         </dependency>
31     <!-- cofig spring jar -->
32         <dependency>
33             <groupId>org.springframework</groupId>
34             <artifactId>spring-core</artifactId>
35             <version>${org.springframework.version}</version>
36         </dependency>
37     ……
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

**redis.properties配置**（WEB-INF/property/redis.properties）

　　从properties文件的内容就知道这个文件是干嘛的了，主要是redis连接池基本配置，详细的配置可以查看redis文档。

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
redis.host=127.0.0.1
redis.port=6379
redis.pass=
  
redis.maxIdle=300
redis.maxActive=600
redis.maxWait=1000
redis.testOnBorrow=true
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

**spring-context.xml**(WEB-INF/config/spring-context.xml)

　　Spring配置，这个也没什么说的，就是springMVC的一些基本配置，开启注解扫描功能和扫描路径。

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 <!-- 激活@Controller模式 -->
 2     <mvc:annotation-driven />
 3     
 4     <context:annotation-config />  
 5     
 6     <!-- 对包中的所有类进行扫描，以完成Bean创建和自动依赖注入的功能 -->
 7     <context:component-scan base-package="com.chr" />
 8 
 9     
10     <!-- 引入redis属性配置文件 -->
11     <import resource="redis-context.xml"/>
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

**redis-context.xml**(WEB/config/redis-context.xml)

　　Spring配置redis，这些配置都比较基本，看文档就好了，但是有一个比较重要的点，就是redistemplate的Serializer配置，在后面通过SDR（Spring Data Redis）封装的一些方法操作Redis时会说到。

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 <!-- scanner redis properties -->
 2     <context:property-placeholder location="classpath:property/redis.properties" />
 3 <!—注意此处注入的是JedisPoolConfig，说明SDR还依赖与Jedis -->
 4     <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
 5         <property name="maxIdle" value="${redis.maxIdle}" />
 6         <property name="maxActive" value="${redis.maxActive}" />
 7         <property name="maxWait" value="${redis.maxWait}" />
 8         <property name="testOnBorrow" value="${redis.testOnBorrow}" />
 9     </bean>
10 
11     <bean id="connectionFactory"
12         class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory"
13         p:host-name="${redis.host}" p:port="${redis.port}" p:password="${redis.pass}"
14         p:pool-config-ref="poolConfig" />
15 
16     <bean id="redisTemplate" class="org.springframework.data.redis.core.StringRedisTemplate">
17         <property name="connectionFactory" ref="connectionFactory" />
18 <!--         如果不配置Serializer，那么存储的时候智能使用String，如果用User类型存储，那么会提示错误User can't cast to String！！！
19  -->        <property name="keySerializer">
20             <bean
21                 class="org.springframework.data.redis.serializer.StringRedisSerializer" />
22         </property>
23         <property name="valueSerializer">
24             <bean
25                 class="org.springframework.data.redis.serializer.JdkSerializationRedisSerializer" />
26         </property>
27     </bean>
28     
29     <bean id="viewResolver"  
30         class="org.springframework.web.servlet.view.InternalResourceViewResolver" />
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

 

**web.xml**

　　web.xml中只配置了spring-context.Xml，这是因为我在spring-context.xml中加了一条语句：<import resource=*"redis-context.xml"*/>，所以看起来有两个配置，其实只需要配置spring-context.xml。这样做的好处是：项目的层次比较清晰，方便后期改动。

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1     <listener>
 2         <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
 3     </listener>
 4 
 5     <context-param>
 6         <param-name>contextConfigLocation</param-name>
 7         <param-value>/WEB-INF/config/spring-context.xml</param-value>
 8     </context-param>
 9 
10      <servlet>
11         <servlet-name>SpringMVC</servlet-name>
12         <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
13         <init-param>
14                  <param-name>contextConfigLocation</param-name>
15                  <param-value>/WEB-INF/config/spring-context.xml</param-value>
16          </init-param>
17          <load-on-startup>2</load-on-startup>
18     </servlet>
19     
20     <servlet-mapping>
21         <servlet-name>SpringMVC</servlet-name>
22         <url-pattern>/</url-pattern>
23     </servlet-mapping>
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

 

下面是Spring MVC的java实现了：

**User.java**（实体类， com.chr.domain.User.java）

　　注意User类必须实现Serializable接口，后面会解释。User类总共定义了三个字段：id、name、password。省略了相应的setter/getter方法。

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 public class User implements Serializable {
 2     private static final long serialVersionUID = 522889572773714584L;
 3     
 4     private String id;
 5     private String name;
 6     private String password;
 7     
 8     public User() {}
 9     
10     public User(String id,String name,String password) {
11         this.id = id;
12         this.name = name;
13         this.password = password;
14     }
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

 

**UserOperationsService.java**（service接口，com.chr.service.UserOperationsService.java）

　　在service接口中定义了两个方法：

　　Add方法用于向redis中添加User实例，getUser则从redis中取出User实例。

```
1 public interface UserOperationsService {
2     void add(User user);
3     User getUser(String key);
4     
5 }
```

 

 

**UserOperationsServiceImpl.java**（service的实现类，实现service借口 com.chr.service.impl. UserOperationsServiceImpl.java）

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 @Service
 2 public class UserOperationsServiceImpl implements UserOperationsService {
 3     @Autowired
 4     private RedisTemplate redisTemplate;
 5 
 6     @Override
 7     public void add(User user) {
 8         // TODO Auto-generated method stub
 9         /*
10          * boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
11          * public Boolean doInRedis(RedisConnection redisConnection) throws
12          * DataAccessException { RedisSerializer<String> redisSerializer =
13          * redisTemplate .getStringSerializer(); byte[] key =
14          * redisSerializer.serialize(user.getId()); byte[] value =
15          * redisSerializer.serialize(user.getName()); return
16          * redisConnection.setNX(key, value); } }); return result;
17          */
18         ValueOperations<String, User> valueops = redisTemplate
19                 .opsForValue();
20         valueops.set(user.getId(), user);
21     }
22 
23 
24     @Override
25     public User getUser(String key) {
26         ValueOperations<String, User> valueops = redisTemplate
27                 .opsForValue();
28         User user = valueops.get(key);
29         return user;
30     }
31 
32 }
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

** 　　RedisTemplate和 Serializer详解**

　　可以看到我在代码中注释掉了一段代码，现在可以解释上面留下的两个问题了，第一个是在redis.xml中配置redistemplate的时候，同时配置了两个Serializer：keySerializer实现了StringRedisSerializer，valueSerializer实现了JdkSerializationRedisSerializer。

　　一、为什么要使用Serializer

　　因为redis是以key-value的形式将数据存在内存中，key就是简单的string，key似乎没有长度限制，不过原则上应该尽可能的短小且可读性强，无论是否基于持久存储，key在服务的整个生命周期中都会在内存中，因此减小key的尺寸可以有效的节约内存，同时也能优化key检索的效率。

　　value在redis中，存储层面仍然基于string，在逻辑层面，可以是string/set/list/map，不过redis为了性能考虑，使用不同的“encoding”数据结构类型来表示它们。(例如：linkedlist，ziplist等)。

　　所以可以理解为，其实redis在存储数据时，都把数据转化成了byte[]数组的形式，那么在存取数据时，需要将数据格式进行转化，那么就要用到序列化和反序列化了，这也就是为什么需要配置Serializer的原因。

　　二、SDR支持的序列化策略：

（详细可查阅API文档）

- JdkSerializationRedisSerializer：
- StringRedisSerializer：
- JacksonJsonRedisSerializer：
- OxmSerializer：

　　其中JdkSerializationRedisSerializer和StringRedisSerializer是最基础的序列化策略，其中“JacksonJsonRedisSerializer”与“OxmSerializer”都是基于stirng存储，因此它们是较为“高级”的序列化(最终还是使用string解析以及构建java对象)。

　　基本推荐使用JdkSerializationRedisSerializer和StringRedisSerializer，因为其他两个序列化策略使用起来配置很麻烦，如果实在有需要序列化成Json和XML格式，可以使用java代码将String转化成相应的Json和XML。

　　三、使用Serializer

　　在本项目中，是在配置文件中直接配置了相应的Serializer，key用的是StringRedisSerializer，value用的是JdkSerializationRedisSerializer，因为在此项目中，key为userId,为String类型，value为user为java类，即POJO，所以使用JdkSerializationRedisSerializer。

　　在redistemplate中直接配置Serializer当然比较方便，因为在后面想redis中存取数据时，就不用再次配置Serializer，但是这仅限于只有一种数据类型的情况，比如在本项目中只有<String userId,User user>类型的数据需要存储，如果有多种数据类型时，在配置文件中配置就显得不方便了，那么我们可以在存取数据时，即Service的实现类存取数据操作时分别指定相应的Serializer。

　　所以在编程时有两种选择：

　　　　1.在redistemplate中配置Serializer（本项目即采用这种方式）

```
ValueOperations<String, User> valueops = redisTemplate
                .opsForValue();
valueops.set(user.getId(), user);
```

 

　　　　2.不在redistemplate中配置Serializer，而是在Service的实现类中单独指定Serializer。就如同UserOperationsServiceImpl.java注释的代码：

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
1 boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
2      public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException { 
3          RedisSerializer<String> redisSerializer = redisTemplate .getStringSerializer(); 
4          byte[] key = redisSerializer.serialize(user.getId());
5          byte[] value = redisSerializer.serialize(user.getName()); 
6          return redisConnection.setNX(key, value); } }); 
7     return result;
8 }
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

 

　　四、Redistemplate

　　SDR官方文档中对Redistemplate的介绍：the template is in fact the central class of the Redis module due to its rich feature set. The template offers a high-level abstraction for Redis interactions.

　　通过Redistemplate可以调用ValueOperations和ListOperations等等方法，分别是对Redis命令的高级封装。

　　但是ValueOperations等等这些命令最终是要转化成为RedisCallback来执行的。也就是说通过使用RedisCallback可以实现更强的功能，SDR文档对RedisCallback的介绍：RedisTemplate and StringRedisTemplate allow the developer to talk directly to Redis through the RedisCallback interface. This gives complete control to the developer as it talks directly to the RedisConnection。

　　具体的使用方法可以参考Api文档。

 

 

**UserController.java**（控制器类,com.chr.controller）

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 @Controller
 2 @RequestMapping(value = "/redis")
 3 public class UserController {
 4     @Autowired
 5     private UserOperationsServiceImpl userOperationsService;
 6     private User user;
 7 
 8     @RequestMapping(value = "/addUser", method = RequestMethod.POST)
 9     public String addUser(
10             @RequestParam(value = "Id", required = true) String Id,
11             @RequestParam(value = "name", required = true) String name,
12             @RequestParam(value = "password", required = true) String password) {
13         user = new User(Id, name, password);
14         userOperationsService.add(user);
15         return "/WEB-INF/jsp/AddUserSuccess.jsp";
16     }
17 
18     @RequestMapping(value = "/addUser", method = RequestMethod.GET)
19     public String addUser() {
20         return "/WEB-INF/jsp/AddUser.jsp";
21     }
22 }
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

　　这里只贴出了部分代码（addUser的代码），剩下的getUser代码类似，可以下载源码查看。

其中分为两种方法，get和post，get方法直接return到表单填写页面，从而实现post到addUser添加User。

 

**AddUser.jsp**

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
<form id="addUser" name="addUser" action="redis/addUser" method="post">
                ID:<input id="Id" name="Id" type="text" /><br/>
                Name:<input id="name" name="name" type="text" /><br/> 
                Password:<input id="password" name="password" type="password" /><br/>
                <input value="添加"
                    type="submit" />
</form>
```

[![复制代码](http://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

 

**三、部署运行**

 

最后部署到Tomcat中，浏览器运行：[http://localhost:8080/redis-web/redis/addUser](http://localhost:8080/redis-web/redis/addUser)

 

![img](http://images.cnitblog.com/i/644226/201406/301712340121586.png)

 

填写信息，单击添加Button后，即跳转到结果页面

![img](http://images.cnitblog.com/i/644226/201406/301712476845195.png)

 

整个项目只是一个展示Spring整合Redis基本使用的Demo，因本人知识有限，如文中有错误或偏颇之处，请各位提出。非常感谢：）



from：

http://www.cnblogs.com/edwinchen/p/3816938.html