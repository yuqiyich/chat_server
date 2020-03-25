## redis 
 * Redis是一个开源的支持网络可基于内存亦可持久化的日志型Key-Value数据库，并提供多种语言的API。它的值（value）可以是字符串（String）、哈希（Hash）、列表（list）、集合（sets）和有序集合（sorted sets）等类型。[redis 官网](https://spring.io/projects/spring-data-redis)
### 安装 （以linux安装为例）
1.使用linux wget下载安装包
```shell script
 wget http://download.redis.io/releases/redis-3.0.0.tar.gz  #其中的版本号请自动使用最新版本号
```
2.将安装包拷贝到安装目录下如/usr/local
  ```shell script
 cp redis-3.0.0.rar.gz /usr/local  #其中的版本号请自动使用最新版本号
   ```
3.解压源码
 ```shell script
 tar -zxvf redis-3.0.0.tar.gz   #其中的版本号请自动使用最新版本号
   ```
4.进入解压后的目录进行编译
```shell script
cd /usr/local/redis-3.0.0  #其中的版本号请自动使用最新版本号
``` 
5.安装
```shell script
 make install 
``` 
6.启动redis 
```shell script
cd src #到编译好的src下
./redis-server #运行该命令启动
```

### redis客户端使用
1.redis连接
  - 如果是当前机器到启动好redis服务下，运行如下脚本进行联接
 ```shell script
  ./redis-cli  #当前目录为redis的启动目录 redis-xxx/src 下
 ``` 
  - 如果是远端机器
```shell script
     redis-cli -h host -p port -a password
```
2.redis客户端操作请查看
 [redis 命令操作手册](https://www.runoob.com/redis/redis-commands.html). 
 
 ### redis项目中使用 （springboot-2.x + redis-2.x ）
1.配置redis连接（redis jar依赖自行百度）
 -配置文件夹加入redis
 ```properties
      ## REDIS (RedisProperties)
      spring.redis.host=10.3.0.43
      spring.redis.password=xxx
      spring.redis.port=6379
 ```
2.redis 项目中Config类配置
   * java 代码配置
```java
/**
 * redis配置类
 **/
@Configuration
@EnableCaching//开启注解式缓存
//继承CachingConfigurerSupport，为了自定义生成KEY的策略。可以不继承。
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 生成key的策略 根据类名+方法名+所有参数的值生成唯一的一个key
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }
    /**
     * 管理缓存
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        //通过Spring提供的RedisCacheConfiguration类，构造一个自己的redis配置类，从该配置类中可以设置一些初始化的缓存命名空间
        // 及对应的默认过期时间等属性，再利用RedisCacheManager中的builder.build()的方式生成cacheManager：
       RedisSerializationContext.SerializationPair<Object> objectSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer());
            
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();  // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        config = config.entryTtl(Duration.ofDays(30))     // 设置缓存的默认过期时间，也是使用Duration设置
                .disableCachingNullValues();     // 不缓存空值
         config.defaultCacheConfig(Thread.currentThread().getContextClassLoader())
                                       .serializeValuesWith(objectSerializationPair);

        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("my-redis-cache1");
        cacheNames.add("my-redis-cache2");

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("my-redis-cache1", config);//my-redis-cache1 类似命名空间
        configMap.put("my-redis-cache2", config.entryTtl(Duration.ofSeconds(120)));//设置命名空间下所有的超时时间

           RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)     // 使用自定义的缓存配置初始化一个cacheManager
                .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                .withInitialCacheConfigurations(configMap)
                .build();
        return cacheManager;
    }
   /**
   生成redis操作模板
*/
@Bean
    public RedisTemplate<Object, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }


   

}
```
* xml 代码配置
  可百度查询

3.项目中使用
 - 注解方式使用
   * @Cacheable  有就用缓存，没有就查出来放入缓存
 ```java
   @Cacheable(value = "fpcache",key = "#id" ,condition="");
 ```
其中value 字段表示redis的存储库也就是命名空间，命名空间可以配置缓存时间 ,key为存在redis里面的key,condition指在某些条件下才缓存表达式为 SpEL 
  * @CachePut 用来存储某些值
 ```java
    @CachePut(value="users",key="") //values 指命名空间，allEntries是不是要全部清除，key清除指定的key的缓存
```
* @CacheEvict 用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作。@CacheEvict可以指定的属性有value、key、condition、allEntries和beforeInvocation。其中value、key和condition的语义与@Cacheable对应的属性类似。即value表示清除操作是发生在哪些Cache上的（对应Cache的名称）；key表示需要清除的是哪个key，如未指定则会使用默认策略生成的key；condition表示清除操作发生的条件。下面我们来介绍一下新出现的两个属性allEntries和beforeInvocation。
 ```java
    @CacheEvict(value="users", allEntries=true ,key=""); //values 指命名空间，allEntries是不是要全部清除，key清除指定的key的缓存
```
 - java 代码使用
   * redisTemplate 使用自定义的操作模板来间接操作redis的原始库。通过redisConfig提供redisTemplate的生成入口见上面的代码 ，然后spring生成一个redis的操作组件 服务
```java
   @Component
   public class RedisService {
       @Autowired
       private RedisTemplate<String, String> redisTemplate;
 /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;




    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     *
     * @param oldKey
     * @param newKey
     */
    public void renameKey(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * newKey不存在时才重命名
     *
     * @param oldKey
     * @param newKey
     * @return 修改成功返回true
     */
    public boolean renameKeyNotExist(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 删除key
     *
     * @param key
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     *
     * @param keys
     */
    public void deleteKey(String... keys) {
        Set<String> kSet = Stream.of(keys).map(k -> k).collect(Collectors.toSet());
        redisTemplate.delete(kSet);
    }

    /**
     * 删除Key的集合
     *
     * @param keys
     */
    public void deleteKey(Collection<String> keys) {
        Set<String> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
        redisTemplate.delete(kSet);
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expireKey(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public void expireKeyAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public void persistKey(String key) {
        redisTemplate.persist(key);
    }
}
```
最后在需要业务的地方使用RedisService 来操作redis的数据库
