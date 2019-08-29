# 3.3 日志

日志在应用开发中是一个非常关键的部分。有经验的工程师能够凭借以往的经验判断出哪里该打印日志、该以何种级别打印日志。这样就能够在线上发生问题的时候快速定位并解决问题，极大的减少应用的运维成本。

使用控制台输出其实也算日志的一种，在容器中会打印到容器的日志文件中。但是，控制台输出过于简单，缺乏日志中级别控制、异步、缓冲等特性，因此在开发中要杜绝使用控制台输出作为日志（System.out.println）。而Java中已经有很多成熟的日志框架供大家使用：

- JDK Logging
- Apache Log4j
- Apache Log4j2
- Logback

此外，还有两个用于实现日志统一的框架：Apache Commons-Logging、SLF4j。与上述框架的不同之处在于，其只是一个门面，并没有日志框架的具体实现,可以认为是日志接口框架。

对于这些日志框架来说，一般会解决日志中的以下问题：

- 日志的级别: 定义日志级别来区分不同级别日志的输出路径、形式等，帮助我们适应从开发调试到部署上线等不同阶段对日志输出粒度的不同需求。
- 日志的输出目的地：包括控制台、文件、GUI组件，甚至是套接口服务器、UNIX Syslog守护进程等。
- 日志的输出格式：日志的输出格式（JSON、XML）。
- 日志的输出优化：缓存、异步等。

这里需要说的是，目前有几个框架提供了占位符的日志输出方式，然而其最终是用indexOf去循环查找再对信息进行拼接的，会消耗CPU。建议使用正确估算大小的StringBuilder拼装输出信息，除非是实在无法确定日志是否输出才用占位符。

## 3.3.1 JDK Logging

JDK Logging就是JDK自带的日志操作类，在java.util.logging包下面，通常被简称为JUL。

### 配置

JDK Logging配置文件默认位于$JAVA_HOME/jre/lib/logging.properties中，可以使用系统属性java.util.logging.config.file指定相应的配置文件对默认的配置文件进行覆盖。

```
handlers= java.util.logging.FileHandler,java.util.logging.ConsoleHandler
.handlers = java.util.logging.FileHandler,java.util.logging.ConsoleHandler #rootLogger使用的Handler
.level= INFO #rootLogger的日志级别

##以下是FileHandler的配置
java.util.logging.FileHandler.pattern = %h/java%u.log
java.util.logging.FileHandler.limit = 50000
java.util.logging.FileHandler.count = 1
java.util.logging.FileHandler.formatter =java.util.logging.XMLFormatter #配置相应的日志Formatter。

##以下是ConsoleHandler的配置
java.util.logging.ConsoleHandler.level = INFO
java.util.logging.ConsoleHandler.formatter =java.util.logging.SimpleFormatter #配置相应的日志Formatter。

#针对具体的某个logger的日志级别配置
me.rowkey.pje.log.level = SEVERE

#设置此logger不会继承成上一级logger的配置
me.rokey.pje.log.logger.useParentHandlers = false 
```

这里需要说明的是logger默认是继承的，如me.rowkey.pje.log的logger会继承me.rowkey.pje的logger配置，可以对logger配置handler和useParentHandlers（默认是为true）属性, 其中useParentHandler表示是否继承父logger的配置。

JDK Logging的日志级别比较多，从高到低为：OFF(2^31-1)—>SEVERE(1000)—>WARNING(900)—>INFO(800)—>CONFIG(700)—>FINE(500)—>FINER(400)—>FINEST(300)—>ALL(-2^31)。

### 使用

JDK Logging的使用非常简单：

```
public class LoggerTest{

    private static final Logger LOGGER = Logger.getLogger(xx.class.getName());
    
    public static void main(String[] args){
        LOGGER.info("logger info");
    }
}
...
```

### 性能优化

JDK Logging是一个比较简单的日志框架，并没有提供异步、缓冲等优化手段。也不建议大家使用此框架。

## 3.3.2 Log4j

Log4j应该是目前Java开发中用的最为广泛的日志框架。

### 配置

Log4j支持XML、Proerties配置，通常还是使用Properties：

```
root_log_dir=${catalina.base}/logs/app/

# 设置rootLogger的日志级别以及appender
log4j.rootLogger=INFO,default

# 设置Spring Web的日志级别
log4j.logger.org.springframework.web = ERROR

# 设置default appender为控制台输出
log4j.appender.default=org.apache.log4j.ConsoleAppender
log4j.appender.default.layout=org.apache.log4j.PatternLayout
log4j.appender.default.layout.ConversionPattern=[%-d{HH\:mm\:ss} %-3r %-5p %l] >> %m (%t)%n

# 设置新的logger，在程序中使用Logger.get("myLogger")即可使用
log4j.logger.myLogger=INFO,A2

# 设置另一个appender为按照日期轮转的文件输出
log4j.appender.A2=org.apache.log4j.DailyRollingFileAppender
log4j.appender.A2.File=${root_log_dir}log.txt
log4j.appender.A2.Append=true
log4j.appender.A2.DatePattern= yyyyMMdd'.txt'
log4j.appender.A2.layout=org.apache.log4j.PatternLayout
log4j.appender.A2.layout.ConversionPattern=[%-d{HH\:mm\:ss} %-3r %-5p %l] >> %m (%t)%n

log4j.logger.myLogger1 = INFO,A3

# 设置另一个appender为RollingFileAppender，能够限制日志文件个数
log4j.appender.A3 = org.apache.log4j.RollingFileAppender
log4j.appender.A3.Append = true
log4j.appender.A3.BufferedIO = false
log4j.appender.dA3.File = /home/popo/tomcat-yixin-pa/logs/pa.log
log4j.appender.A3.Encoding = UTF-8
log4j.appender.A3.layout = org.apache.log4j.PatternLayout
log4j.appender.A3.layout.ConversionPattern = [%-5p]%d{ISO8601}, [Class]%-c{1}, %m%n
log4j.appender.A3.MaxBackupIndex = 3 #最大文件个数
log4j.appender.A3.MaxFileSize = 1024MB
```

如果Log4j文件不直接在classpath下的话，可以使用PropertyConfigurator来进行配置：

```
PropertyConfigurator.configure("...");

```

Log4j的日志级别相对于JDK Logging来说，简化了一些：DEBUG < INFO < WARN < ERROR < FATAL。

这里的logger默认是会继承父Logger的配置（rootLogger是所有logger的父logger），如上面myLogger的输出会同时在控制台和文件中出现。如果不想这样，那么只需要如下设置:

```
log4j.additivity.myLogger=false
```

### 使用

程序中对于Log4j的使用也非常简单：

```
import org.apache.log4j.Logger;


private static final Logger LOGGER = Logger.getLogger(xx.class.getName());
...
LOGGER.info("logger info");
...
```

这里需要注意的是，虽然Log4j可以根据配置文件中日志级别的不同做不同的输出，但由于字符串创建或者拼接也是耗资源的，因此，下面的用法是不合理的。

```
LOGGER.debug("...");
```

合理的做法应该是首先判断当前的日志级别是什么，再去做相应的输出，如：

```
if(LOGGER.isDebugEnabled()){
    LOGGER.debug("...");
}
```
当然，如果是必须输出的日志可以不做此判断，比如catch异常打印错误日志的地方。

### 性能优化

Log4j为了应对某一时间里大量的日志信息进入Appender的问题提供了缓冲来进一步优化性能：

``` 
log4j.appender.A3.BufferedIO=true   
#Buffer单位为字节，默认是8K，IO BLOCK大小默认也是8K 
log4j.appender.A3.BufferSize=8192 
```

以上表示当日志内容达到8k时，才会将日志输出到日志输出目的地。  

除了缓冲以外，Log4j还提供了AsyncAppender来做异步日志。但是AsyncAppender只能够通过xml配置使用：

```
<appender name="A2"
   class="org.apache.log4j.DailyRollingFileAppender">
   <layout class="org.apache.log4j.PatternLayout">
       <param name="ConversionPattern" value="%m%n" />
   </layout>
   <param name="DatePattern" value="'.'yyyy-MM-dd-HH" />        
   <param name="File" value="app.log" />
   <param name="BufferedIO" value="true" />
   <!-- 8K为一个写单元 -->
   <param name="BufferSize" value="8192" />
</appender>

<appender name="async" class="org.apache.log4j.AsyncAppender">
   <appender-ref ref="A2"/>
</appender>
```

## 3.3.3 Log4j2

2015年8月，官方正式宣布Log4j 1.x系列生命终结，推荐大家升级到Log4j2，并号称在修正了Logback固有的架构问题的同时，改进了许多Logback所具有的功能。Log4j2与Log4j1发生了很大的变化，并不兼容。并且Log4j2不仅仅提供了日志的实现，也提供了门面，目的是统一日志框架。其主要包含两部分：

- log4j-api： 作为日志接口层，用于统一底层日志系统
- log4j-core : 作为上述日志接口的实现，是一个实际的日志框架

### 配置

Log4j2的配置方式只支持XML、JSON以及YAML，不再支持Properties文件,其配置文件的加载顺序如下：

- log4j2-test.json/log4j2-test.jsn
- log4j2-test.xml
- log4j2.json/log4j2.jsn文件
- log4j2.xml

如果想要自定义配置文件位置，需要设置系统属性log4j.configurationFile。

```
System.setProperty("log4j.configurationFile", "...");
或者
-Dlog4j.configurationFile="xx"
```

配置文件示例：

```
<!--log4j2.xml-->
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
<Appenders>
  <Console name="Console" target="SYSTEM_OUT">
    <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
  </Console>
  <File name="File" fileName="app.log" bufferedIO="true" immediateFlush="true">
    <PatternLayout>
      <pattern>%d %p %C{1.} [%t] %m%n</pattern>
    </PatternLayout>
  </File>
  <RollingFile name="RollingFile" fileName="logs/app.log"
                     filePattern="log/$${date:yyyy-MM}/app-%d{MM-dd-yyyy}-%i.log.gz">
      <PatternLayout pattern="%d{yyyy-MM-dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n"/>
      <SizeBasedTriggeringPolicy size="50MB"/>
      <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件，这里设置了20 -->
      <DefaultRolloverStrategy max="20"/>
  </RollingFile>
</Appenders>
<Loggers>
  <logger name="myLogger" level="error" additivity="false">
    <AppenderRef ref="File" />
  </logger>
  <Root level="debug">
    <AppenderRef ref="Console"/>
  </Root>
</Loggers>
</Configuration>
```

上面的monitorInterval使得配置变动能够被实时监测并更新，且能够在配置发生改变时不会丢失任何日志事件;additivity和Log4j一样也是为了让Looger不继承父Logger的配置；Configuration中的status用于设置Log4j2自身内部的信息输出，当设置成trace时，你会看到Log4j2内部各种详细输出。 

Log4j2在日志级别方面也有了一些改动：TRACE < DEBUG < INFO < WARN < ERROR < FATAL, 并且能够很简单的自定义自己的日志级别。

```
<CustomLevels>
    <CustomLevel name="NOTICE" intLevel="450" />
    <CustomLevel name="VERBOSE" intLevel="550" />
</CustomLevels>
```

上面的intLevel值是为了与默认提供的标准级别进行对照的。

### 使用

使用方式也很简单：

```
private static final Logger LOGGER = LogManager.getLogger(xx.class);

LOGGER.debug("log4j debug message");
```

这里需要注意的是其中的Logger是log4j-api中定义的接口，而Log4j1中的Logger则是类。

相比起之前我们需要先判断日志级别，再输出日志，Log4j2提供了占位符功能：

```
LOGGER.debug("error: {} ", e.getMessage());
```

### 性能优化

在性能方面，Log4j2引入了基于LMAX的Disruptor的无锁异步日志实现进一步提升异步日志的性能：

```
<AsyncLogger name="asyncTestLogger" level="trace" includeLocation="true">
    <AppenderRef ref="Console"/>
</AsyncLogger>
```

需要注意的是，由于默认日志位置信息并没有被传给异步Logger的I/O线程，因此这里的includeLocation必须要设置为true。

和Log4j一样，Log4j2也提供了缓冲配置来优化日志输出性能。

```
<Appenders>
  <File name="File" fileName="app.log" bufferedIO="true" immediateFlush="true">
    <PatternLayout>
      <pattern>%d %p %C{1.} [%t] %m%n</pattern>
    </PatternLayout>
  </File>
</Appenders>
```

## 3.3.4 Logback

Logback是由Log4j创始人设计的又一个开源日志组件，相对Log4j而言，在各个方面都有了很大改进。

Logback当前分成三个模块：

- logback-core是其它两个模块的基础模块。
- logback-classic是Log4j的一个改良版本。logback-classic完整实现SLF4J API使你可以很方便地更换成其它日志系统如Log4j或JDK Logging。
- logback-access访问模块与Servlet容器集成提供通过HTTP来访问日志的功能。

### 配置

Logback的配置文件如下：

```
<!--logback.xml-->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="root_log_dir" value="${catalina.base}/logs/app/"/>

    <appender name="ROLLING_FILE_APPENDER" class="ch.qos.logback.core.rolling.RollingFileAppender">
       <File>${root_log_dir}app.log</File>
       <Append>true</Append>
       <encoder>
           <pattern>%date [%level] [%thread] %logger{80} [%file : %line] %msg%n</pattern>
       </encoder>
       <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
           <fileNamePattern>${root_log_dir}app.log.%d{yyyy-MM-dd}.%i</fileNamePattern>
           <maxHistory>30</maxHistory> #只保留最近30天的日志文件
           <TimeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">#每天的日志按照100MB分割
                <MaxFileSize>100MB</MaxFileSize>
            </TimeBasedFileNamingAndTriggeringPolicy>
            <totalSizeCap>20GB</totalSizeCap>#日志总的大小上限，超过此值则异步删除旧的日志
       </rollingPolicy>
    </appender>
    
    <appender name="ROLLING_FILE_APPENDER_2" class="ch.qos.logback.core.rolling.RollingFileAppender">
       <File>${root_log_dir}mylog.log</File>
       <Append>true</Append>
       <encoder>
           <pattern>%date [%level] [%thread] %logger{80} [%file : %line] %msg%n</pattern>
       </encoder>
       #下面的日志rolling策略和ROLLING_FILE_APPENDER的等价，保留最近30天的日志，每天的日志按照100MB分隔，日志总的大小上限为20GB
       <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>mylog.log-%d{yyyy-MM-dd}.%i</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
     <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
       <encoder>
         <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
       </encoder>
     </appender>
        
    <logger name="myLogger" level="INFO" additivity="false">
        <appender-ref ref="ROLLING_FILE_APPENDER" />
    </logger>
        
     <root level="DEBUG">          
       <appender-ref ref="STDOUT" />
     </root>  

</configuration>
```

Logback的配置文件读取顺序（默认都是读取classpath下的）：logback.groovy -> logback-test.xml -> logback.xml。如果想要自定义配置文件路径，那么只有通过修改logback.configurationFile的系统属性。

```
System.setProperty("logback.configurationFile", "...");
或者
-Dlogback.configurationFile="xx"
```

Logback的日志级别：TRACE < DEBUG < INFO < WARN < ERROR。如果logger没有被分配级别，那么它将从有被分配级别的最近的祖先那里继承级别。root logger 默认级别是 DEBUG。

Logback中的logger同样也是有继承机制的。配置文件中的additivit也是为了不去继承rootLogger的配置，从而避免输出多份日志。

为了方便Log4j到Logback的迁移，官网提供了log4j.properties到logback.xml的转换工具：<https://logback.qos.ch/translator/>。

### 使用

Logback由于是天然与SLF4J集成的，因此它的使用也就是SLF4J的使用。

```
import org.slf4j.LoggerFactory;

private static final Logger LOGGER=LoggerFactory.getLogger(xx.class);

LOGGER.info(" this is a test in {}", xx.class.getName())
```

SLF4J同样支持占位符。

此外，如果想要打印json格式的日志（例如，对接日志到Logstash中），那么可以使用logstash-logback-encoder做为RollingFileAppender的encoder。

```
<encoder class="net.logstash.logback.encoder.LogstashEncoder" >
...
</encoder>
```

### 性能优化

Logback提供了AsyncAppender进行异步日志输出，此异步appender实现上利用了队列做缓冲，使得日志输出性能得到提高。

```
<appender name="FILE_APPENDER" class="ch.qos.logback.core.rolling.RollingFileAppender">
      <File>${root_log_dir}app.log</File>
      <Append>true</Append>
      <encoder>
          <pattern>%date [%level] [%thread] %logger{80} [%file : %line] %msg%n</pattern>
      </encoder>
      <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
          <fileNamePattern>${root_log_dir}app.log.%d</fileNamePattern>
      </rollingPolicy>
</appender>
<appender name ="ASYNC" class= "ch.qos.logback.classic.AsyncAppender">  
       <discardingThreshold >0</discardingThreshold>  
       
       <queueSize>512</queueSize>  
       
       <appender-ref ref ="FILE_APPENDER"/>  
</appender>  
       
```

这里需要特别注意以下两个参数的配置：

- queueSize：队列的长度,该值会影响性能，需要合理配置。
- discardingThreshold：日志丢弃的阈值，即达到队列长度的多少会丢弃TRACT、DEBUG、INFO级别的日志，默认是80%，设置为0表示不丢弃日志。

此外，由于是异步输出，为了保证日志一定会被输出以及后台线程能够被及时关闭，在应用退出时需要显示关闭logback。有两种方式：

- 在程序退出的地方（ServletContextListener的contextDestroyed方法、Spring Bean的destroy方法）显式调用下面的代码。

    ```
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.stop();
    ```
    
- 在logback配置文件里，做如下配置。

    ```
    <configuration>
   
        <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/>
        .... 
    </configuration>
    ```

## 3.3.5 日志门面

前面的四个框架是实际的日志框架。对于开发者而言，每种日志都有不同的写法。如果我们以实际的日志框架来进行编写，代码就限制死了，之后就很难再更换日志系统，很难做到无缝切换。

Java开发中经常提到面向接口编程，所以我们应该是按照一套统一的API来进行日志编程，实际的日志框架来实现这套API，这样的话，即使更换日志框架，也可以做到无缝切换。

这就是Commons-Logging与SLF4J这种日志门面框架的初衷。

### Apache Commons-Logging

Apache Commons-Logging经常被简称为JCL，是Apache开源的日志门面框架。Spring中使用的日志框架就是JCL，使用起来非常简单。

```
import org.apache.commons.logging.LogFactory;

private static final Log LOGGER = LogFactory.getLog(xx.class);

LOGGER.info("...");
```

使用JCL需要先引入JCL的依赖：

```
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>xx</version>
</dependency>
```

再来看一下如何让JCL使用其他日志实现框架:

1. 这里当没有其他日志jar包存在的时候，JCL有自己的默认日志实现，默认的实现是对JUL的包装，即当没有其他任何日志包时，通过JCL调用的就是JUL做日志操作。
2. 使用Log4j作为日志实现框架，那么只需要引入Log4j的jar包即可。
3. 使用Log4j2作为日志实现，那么除了Log4j2的jar包，还需要引入Log4j2与Commons-Logging的集成包（使用SPI机制提供了自己的LogFactory实现）：

    ```
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-jcl</artifactId>
        <version>xx</version>
    </dependency>
    ```
    
3. 使用Logback作为日志实现，那么由于Logback的调用是通过SLF4J的，因此需要引入jcl-over-slf4j包（直接覆盖了JCL的类），并同时引入SLF4J以及Logback的jar包。

    ```
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <version>xx</version>
    </dependency>
    ```

### SLF4J

SLF4J（Simple Logging Facade for Java）为Java提供的简单日志Facade。允许用户以自己的喜好，在工程中通过SLF4J接入不同的日志实现。与JCL不同的是，SLF4J只提供接口，没有任何实现（可以认为Logback是默认的实现）。

SLF4J的使用前提是引入SLF4J的jar包:

```
<!-- SLF4J -->
<dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-api</artifactId>
   <version>xx</version>
</dependency>
```

再看一下SLF4J如何和其他日志实现框架集成。

1. 使用JUL作为日志实现，需要引入slf4j-jdk14包。

    ```
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-jdk14</artifactId>
        <version>xx</version>
    </dependency>
    ```

1. 使用Log4j作为日志实现，需要引入slf4j-log4j12和log4j两个jar包。

    ```
    <!-- slf4j-log4j -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>xx</version>
    </dependency>
    
    <!-- log4j -->
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>xx</version>
    </dependency>
    ```

1. 使用Log4j2作为日志实现，需要引入log4j-slf4j-impl依赖。

    ```
    <!-- log4j2 -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>xx</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>xx/version>
    </dependency>
    <!-- log4j-slf4j-impl （用于log4j2与slf4j集成） -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>xx</version>
    </dependency>
    ```
    
1. 使用Logback作为日志实现，只需要引入logback包即可。

## 3.3.6 日志集成

上面说到了四种日志实现框架和两种日志门面框架。面对这么多的选择，即便是一个刚刚开始做的应用，也会由于依赖的第三方库使用的日志框架五花八门而造成日志配置和使用上的烦恼。得益于JCL和SLF4J，我们可以很容易的把日志都统一为一种实现，从而可以进行集中配置和使用。这里就以用Logback统一日志实现为例：

1. 配置好Logback的依赖：

    ```
    <!-- slf4j-api -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>xx</version>
    </dependency>
    <!-- logback -->
    <dependency> 
        <groupId>ch.qos.logback</groupId> 
        <artifactId>logback-core</artifactId> 
        <version>xx</version> 
    </dependency>
    <!-- logback-classic（已含有对slf4j的集成包） --> 
    <dependency> 
        <groupId>ch.qos.logback</groupId> 
        <artifactId>logback-classic</artifactId> 
        <version>xx</version> 
    </dependency>
    ```

1. 切换Log4j到SLF4J

    ```
    <dependency>
       <groupId>org.slf4j</groupId>
       <artifactId>log4j-over-slf4j</artifactId>
       <version>xx</verison>
   </dependency>
    ```
    
1. 切换JUL到SLF4J

    ```
    <dependency>
       <groupId>org.slf4j</groupId>
       <artifactId>jul-to-slf4j</artifactId>
       <version>xx</verison>
    </dependency>
    ```
    
1. 切换JCL到SLF4J
    
    ```
    <dependency>
       <groupId>org.slf4j</groupId>
       <artifactId>jcl-over-slf4j</artifactId>
       <version>xx</verison>
    </dependency>
    ```

这里需要注意的是，做了以上配置后，务必要排除其他日志包的存在，如Log4j。此外，在日常开发中经常由于各个依赖的库间接引入了其他日志库，造成日志框架的循环转换。比如同时引入了log4j-over-slf4j和slf4j-log4j12的情况，当使用SLF4J调用日志操作时就会形成循环调用。

笔者目前比较推崇的是使用SLF4J统一所有框架接口，然后都转换到Logback的底层实现。但这里需要说明的是Logback的作者是为了弥补Log4j的各种缺点而优化实现了SLF4J以及Logback，但不知为何作者又推出了Log4j2以期取代Log4j和Logback。所以，如果是一个新的项目，那么直接跳过Log4j和Logback选择Log4j2也是一个不错的选择, 官网也提供了Log4j到Log4j2的迁移说明。


