# 4.3 使用Spring Boot快速开发

本章开始提到Spring现在变得越来越复杂，越来越不好上手。这一点Spring Source自己也注意到了，因此推出了Spring Boot，旨在简化使用Spring的门槛，大大降低Spring的配置工作，并且能够很容易地将应用打包为可独立运行的程序（即不依赖于第三方容器，可以独立以jar或者war包的形式运行）。其带来的开发效率的提升使得Spring Boot被看做至少近5年来Spring乃至整个Java社区最有影响力的项目之一，也被人看作是Java EE开发的颠覆者。另一方面来说，Spring Boot也顺应了现在微服务（MicroServices）的理念，可以用来构建基于Spring框架的可独立部署应用程序。

## 4.3.1 使用
    
一个简单的pom配置示例如下：
    
```
<parent>        
   <groupId>org.springframework.boot</groupId>        
   <artifactId>spring-boot-starter-parent</artifactId>        
   <version>1.4.7.RELEASE</version>
</parent>
    
...
    
<dependencies>        
   <dependency>                
       <groupId>org.springframework.boot</groupId>                
       <artifactId>spring-boot-starter-web</artifactId>        
   </dependency>
</dependencies>

<build>
    <plugins>
       <plugin>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-maven-plugin</artifactId>
           <configuration>
               <executable>true</executable>
            </configuration>
       </plugin>
    </plugins>
</build>
```
    
使用spring-boot-starter-parent作为当前项目的parent将Spring Boot应用相关的一系列依赖（dependency）、插件（plugins）等等配置共享；添加spring-boot-starter-web这个依赖，是为了构建一个独立运行的Web应用；spring-boot-maven-plugin用于将Spring Boot应用以可执行jar包的形式发布出去。

接着可以添加相应的Controller实现：
    
```
@RestController  
public class MyController {
@RequestMapping("/")
   public String hello() {
   	return "Hello World!";
   }
}
```

这里的RestController是一个复合注解，包括@Controller和@ResponseBody。

最后，要让Spring Boot可以独立运行和部署，我们需要一个Main方法入口， 比如：

```   
@SpringBootApplication
public class BootDemo extends SpringBootServletInitializer{    
   public static void main(String[] args) throws Exception {        
       SpringApplication.run(BootDemo.class, args);    
   }
}
```

使用mvn package打包后（可以是jar，也可以是war），java -jar xx.war/jar即可运行一个Web项目，而之所以继承SpringBootServletInitializer是为了能够让打出来的war包也可以放入容器中直接运行，其加载原理在3.4.4节的零XML配置中讲过。

这里需要注意上面spring-boot-maven-plugin这个插件将executable配置为了true，此种配置打出来的jar/war包其压缩格式并非传统的jar/war包，实际上是一个bash文件，可以作为shell脚本直接执行，解压的话需要使用unzip命令。
    
从最根本上来讲，Spring Boot就是一些库和插件的集合，屏蔽掉了很多配置加载、打包等自动化工作，其底层还是基于Spring的各个组件。
    
这里需要注意的是，Spring Boot推崇对项目进行零xml配置。但是就笔者看来，相比起注解配置是糅杂在代码中，每次更新都需要重新编译，XML这种和代码分离的方式耦合性和可维护性则显得更为合理一些，而且在配置复杂时也更清晰。因此，采用Java Config作为应用和组件扫描（component scan）入口，采用XML做其他的配置是一种比较好的方式。此外，当集成外部已有系统的时候， 通过XML集中明确化配置也是更为合理的一种方式。

## 4.3.2 原理浅析

![](media/spring-boot-process.png)

Spring Boot的基础组件之一就是4.1讲过的一些注解配置，除此之外，它也提供了自己的注释。其总体的运行流程如上图所示。

1. @EnableAutoConfiguration

    这个Annotation就是Java Config的典型代表，标注了这个Annotation的Java类会以Java代码的形式（对应于XML定义的形式）提供一系列的Bean定义和实例，结合AnnotationConfigApplicationContext和自动扫描的功能，就可以构建一个基于Spring容器的Java应用了。
    
    @EnableAutoConfiguration的定义信息如下 ：
    
    ```
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @AutoConfigurationPackage
    @Import(EnableAutoConfigurationImportSelector.class)
    public @interface EnableAutoConfiguration {
    ```
    
    标注了此注解的类会发生一系列初始化动作：
    
    - SpringBoot扫描到@EnableAutoConfiguration注解时，就使用Spring框架的SpringFactoriesLoader去扫描classpath下所有META-INF/spring.factories文件的配置信息（META-INF/spring.providers声明了当前Starter依赖的Jar包）。其中包括一些callback接口（在前中后等不同时机执行）：

        - org.springframework.boot.SpringApplicationRunListener
        - org.springframework.context.ApplicationContextInitializer
        - org.springframework.context.ApplicationListener
    
    - 然后Spring Boot加载符合当前场景需要的配置类型并供当前或者下一步的流程使用，这里说的场景就是提取以 org.springframework.boot.autoconfigure.EnableAutoConfiguration作为key标志的一系列Java配置类，然后将这些Java配置类中的Bean定义加载到Spring容器中。

    此外，我们可以使用Spring3系列引入的@Conditional，通过像@ConditionalOnClass、@ConditionalOnMissingBean等具体的类型和条件来进一步筛选通过SpringFactoriesLoader加载的类。
    
2. Spring Boot启动

    每一个Spring Boot应用都有一个入口类，在其中定义main方法，然后使用SpringApplication这个类来加载指定配置并运行SpringBoot Application。如上面写过的入口类：
    
    ```   
    @SpringBootApplication
    public class BootDemo extends SpringBootServletInitializer{    
       public static void main(String[] args) throws Exception {        
           SpringApplication.run(BootDemo.class, args);    
       }
    }
    ```
    
    @SpringBootApplication注解是一个复合注解，包括了@Configuraiton、@EnableAutoConfiguration以及@ComponentScan。通过SpringApplication的run方法，Spring就使用BootDemo作为Java配置类来读取相关配置、加载和扫描相关的bean。
    
    这样，基于@SpringBootApplication注解，Spring容器会自动完成指定语义的一系列工作，包括@EnableAutoConfiguration要求的东西，如：从SpringBoot提供的多个starter模块中加载Java Config配置（META-INF/spring.factories中声明的xxAutoConfiguration），然后将这些Java Config配置筛选上来的Bean定义加入Spring容器中，再refresh容器。一个Spring Boot应用即启动完成。
    
## 4.3.3 模块组成

Spring Boot是由非常多的模块组成的，可以通过pom文件引入进来。EnableAutoConfiguration机制会进行插件化加载进行自动配置，这里模块化机制的原理主要是通过判断相应的类/文件是否存在来实现的。其中几个主要的模块如下:

1. spring-boot-starter-web

    此模块就是标记此项目是一个Web应用，Spring Boot会自动准备好相关的依赖和配置。
    
    这里Spring Boot默认使用Tomcat作为嵌入式Web容器，可以通过声明spring-boot-starter-jetty的dependency来换成Jetty。
    
1. spring-boot-starter-logging

    Spring Boot对此项目开启SLF4J和Logback日志支持。
    
1. spring-boot-starter-redis

     Spring Boot对此项目开启Redis相关依赖和配置来做数据存储。
     
1. spring-boot-starter-jdbc

     Spring Boot对此项目开启JDBC操作相关依赖和配置来做数据存储。
     
     这里需要说明的是，Spring Boot提供的功能非常丰富，因此显得非常笨重复杂。其实依赖于模块插件化机制，我们可以只配置自己需要使用的功能，从而对应用进行瘦身，避免无用的配置影响应用启动速度。
     
## 4.3.4 总结

Spring Boot给大家使用Spring做后端应用开发带来了非常大的便利，能够大大提高搭建应用雏形框架的速度，只需要关注实现业务逻辑即可。其“黑魔法”一样的插件化机制使得能够根据自己的需要引入所需的组件，提供了非常好的灵活性。如果非遗留Spring项目，直接使用Spring Boot是比较好的选择；遗留项目也可以通过配置达到无缝结合。

