## redis 
 * Redis是一个开源的支持网络可基于内存亦可持久化的日志型Key-Value数据库，并提供多种语言的API。它的值（value）可以是字符串（String）、哈希（Hash）、列表（list）、集合（sets）和有序集合（sorted sets）等类型。
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
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();  // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        config = config.entryTtl(Duration.ofDays(30))     // 设置缓存的默认过期时间，也是使用Duration设置
                .disableCachingNullValues();     // 不缓存空值

        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("my-redis-cache1");
        cacheNames.add("my-redis-cache2");

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("my-redis-cache1", config);
        configMap.put("my-redis-cache2", config.entryTtl(Duration.ofSeconds(120)));

        RedisSerializationContext.SerializationPair<Object> objectSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer());
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)     // 使用自定义的缓存配置初始化一个cacheManager
                .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                .withInitialCacheConfigurations(configMap)
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig(Thread.currentThread().getContextClassLoader())
                                .serializeValuesWith(objectSerializationPair)
                )
                .build();
        return cacheManager;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
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

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        return stringRedisTemplate;
    }

}
```
3.项目中使用
 - 注解方式使用
   * @Cacheable  有就用缓存，没有就查出来放入缓存
 ```java
   @Cacheable(value = "fpcache",key = "#id")//其中value 字段表示redis的存储库 key为存在redis的key
```
   * 







