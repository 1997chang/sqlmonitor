# 集成Mybatis SQL监控  - SQLMonitor

主要使用Mybatis的插件功能，完成SQL语句的监控，通知，存储等功能。

快速定位慢SQL语句有哪个Mapper的哪个方法造成。

本文档主要Mybatis默认集成，使用配置文件配置，或者自己手动加入该插件。

当使用Spring boot自动集成，参看[spring boot sqlMonitor集成](https://github.com/1997chang/sqlmonitor-spring-boot)

The SQL Monitor can help you quickly find SQL statement executor point with Mybatis. Because we hard find SQL statement from own application position in using Mybatis. 

## 支持版本 [MyBatis 3.2.0+](https://github.com/mybatis/mybatis-3)

## 主要解决

当使用MySQL等其他数据库监控时，会告诉你完成的SQL语句耗时严重问题。但是我们很难根据一条完整的SQL语句去定位哪个Mapper的哪个方法造成的。因为Mapper文件中可能包含参数条件判断、迭代语句等，又或者在其他Mapper中有相似SQL语句。

而该插件将会告诉你：完整的SQL语句，Mapper文件、以及调用栈、执行时间等信息。

从而快速定位数据库监控中SQL语句是由哪个Mapper的哪个方法造成。

## 主要功能

- 存储执行SQL语句的信息：现集成：Logger打印，ES存储。默认为Logger。也可以自己实现，只要实现`com.moxiao.sqlmonitor.store.StorePolicy`类
- 通知慢SQL语句的执行信息：现集成：钉钉通知。或者实现`com.moxiao.sqlmonitor.notice.NoticePolicy`类

## 如何使用

### 1. 添加依赖
```xml
<dependency>
    <groupId>com.moxiao</groupId>
    <artifactId>sqlmonitor-mybatis</artifactId>
    <version>最新版本</version>
</dependency>
```

注意：当使用钉钉进行通知的时候：如何配置钉钉机器人的配置信息依赖，还要添加okhttp3依赖，版本要求4.1.0以上
```xml
<!-- https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.3</version>
</dependency>

```


### 2. 设置Mybatis插件

#### 2.1). 在MyBatis配置文件XML中配置拦截器插件
```xml
<plugins>
    <plugin interceptor="com.moxiao.sqlmonitor.interceptor.SqlMonitorInterceptor">
        <!-- 使用下面的方式配置参数，后面会有所有的参数介绍 -->
        <property name="param1" value="value1"/>
        <!-- 例如：SQL5秒表示慢SQL，钉钉通知地址，ES配置文件的地址，存储策略
        <property name="executeTimeLimit" value="5000"/>
        <property name="slowSqlMonitor" value="5000"/>
        <property name="dingdingConfig.secret" value="SECd115fa66c6782b3e6bd361a73ee9a66bd53bb3697466cbb6457c27e335799f1a"/>
        <property name="dingdingConfig.accessToken" value="10f12cead3ce688dc030a35ad584f90aed07af401bd918b652c99a2180e77f2b"/>
        <property name="esConfig.uri" value="https://localhost:9201,https://localhost:9202,https://localhost:9203"/>
        <property name="esConfig.username" value="elastic"/>
        <property name="esConfig.password" value="elastic"/>
        <property name="esConfig.fingerPrint" value="11be02bc53f0b5f0cb5aae184411f76b916437508c7938f1963a2b00e63ea9e5"/>
        <property name="storePolicy" value="LOGGER,ES"/>-->
    </plugin>
</plugins>
```

#### 2.2). 在Spring Bean的XML配置文件配置拦截器
```xml
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
   <property name="plugins">
      <array>
         <bean class="com.moxiao.sqlmonitor.interceptor.SqlMonitorInterceptor">
            <property name="properties">
               <!--使用下面的方式配置参数，一行配置一个 -->
               <value>
                  params=value1
               </value>
            </property>
         </bean>
      </array>
   </property>
</bean>
```


## 参数描述

1. **executeTimeLimit**：慢SQL语句阈值，默认5000ms，大于该值成为慢SQL
2. **slowSqlMonitor**：慢SQL监控定时任务的时间间隔，默认5000ms
3. **monitorStackPrefix**：执行栈匹配的前缀，满足该前缀的才会显示。从而减少执行栈显示的个数，配置从自己主项目的包名
4. **monitorStackClass**：执行栈匹配的类包名，配置为类的名称。即：将类的包名作为匹配的前缀
5. **maxStackLength**：执行栈的最大长度，默认30
6. **storePolicy**：SQL语句的存储策略，默认**LOGGER**。自带的可选参数有：ES，LOGGER。也可以是自提供的类名，要实现`com.moxiao.sqlmonitor.store.StorePolicy`类。如果有多个，中间逗号隔开。
7. **noticePolicy**：慢SQL的通知策略，默认**DINGDING**，自带的可选参数有：DINGDING。也可以是自提供的类名，要实现`com.moxiao.sqlmonitor.notice.NoticePolicy`。如果有多个，中间逗号隔开。
8. **esConfig**：ES配置
   1. **uri**：ES的通信地址，带https或者http，多个中间用逗号隔开，
   2. **username**：ES的用户名
   3. **password**：ES的密码
   4. **certFile**：ES证书的地址,ES8之后开始使用证书
   5. **fingerPrint**：ES的指纹
   6. **versionLessEleven**：ES版本是否低于7.11，不包含7.11。默认为FALSE
   7. **numberOfShards**：分片的数量，默认为：3
   8. **numberOfReplicas**：ES索引的备份数量，默认为：1
   9. **indexName**：ES存储的索引名称，默认为：slow-sql-monitor
   
9. **dingdingConfig**：钉钉机器人
   1. **secret**：钉钉机器人的秘钥
   2. **accessToken**：钉钉机器人的token
   3. **atMobiles**：钉钉@的人的手机号，多个用逗号隔开
   4. **collectionAble**：钉钉通知是否进行收集。默认为FALSE。FALSE：一个慢SQL通知一次，TRUE：多个慢SQL合并通知一次，钉钉通知有限制。
   5. **type**：钉钉通知的类型，默认MARKDOWN。可选参数TEXT，MARKDOWN。
   
10. **threadPoolConfig**：线程池配置
    1. **corePoolSize**：核心线程池数量，默认为10
    2. **keepAliveTime**：线程保持的时间，单位ms，默认为60_000
    3. **queueCapacity**：阻塞队列的最大长度，默认为：Integer.MAX_VALUE
    4. **rejectedExecutionPolicy**：拒绝执行的策略，默认为LOGGER，可选参数包含：ABORT,LOGGER,DISCARD,DISCARD_OLDEST,CALLER_RUNNER
    5. **daemon**：是否守护线程，默认为TRUE
    6. **waitTasksToCompleteOnShutdown**：是否等待任务完成之后关闭线程，默认FALSE
    7. **awaitTerminationSeconds**：如果等待任务完成，则最大等待时间，默认为0