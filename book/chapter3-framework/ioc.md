# 3.1 依赖注入

IOC，控制翻转（Inversion of Control），又叫依赖注入（Dependency Inject）。即将代码里对象之间的依赖关系转移到容器中，这样就能够很灵活的通过面向接口的编程方式改变真正的实现类。

如下是用代码来维护依赖关系的，如果此时后面有了另外的IUser的实现类，那么如果要使用这个新的实现类则需要修改代码重新set。

```
public interface IUser{
    void say();
}

public class AdminUser implements IUser{
    public void say(){
        System.out.println("I'm admin");
    }
}

public class IOCTest{
    private IUser user;
    
    public void setUser(IUser user){
        this.user = user;
    }
    
    public IUser getUser(){
        return this.user;
    }
    
    public void test(){
        this.user.say();
    }
    
    public static void main(String[] args){
        IOCTest test = new IOCTest();
        test.setUser(new AdminUser());
        
        test.test();
    }
}
```
而IOC的作用就是讲这些依赖关系的维护从代码里拿出来，通过容器来维护这些关系。一个典型的例子，伪代码如下：

```
IOCContainer container = new IOCContainer("..");//可以通过一个上下文配置文件，也可以通过扫描注解
    
IOCTest iocTest = container.getInstance("iocTest");
iocTes.test();
```
    
一个典型的上下文配置文件如下：

```
<bean class="IOCTest" name="iocTest">
   <property name="user" ref="realUser"/>
</bean>
<bean class="AdminUser" name="realUser" />
```
    
这样当想要改变实现类时只需要修改配置文件即可。

对于依赖注入，JSR330（Dependency Injection for Java）做了一些规范。该规范主要是面向依赖注入使用者，而对注入器实现、配置并未作详细要求。目前Spring、Guice已经开始兼容该规范。JSR-330规范并未按JSR惯例发布规范文档，只发布了API源码。其指定了获取对象的一种方法，该方法与构造器、工厂以及服务定位器（例如 JNDI）这些传统方法相比可以获得更好的可重用性、可测试性以及可维护性。此方法的处理过程就是依赖注入。

目前市面上常见的IOC框架有以下几个：

- Google Guice
- PicoContainer
- Dagger
- SpringFramework

其中，兼容JSR330标准的Guice易用性最好；Pico比较轻量，不过需要手工添加Bean类到容器，用起来有点烦；Dagger使用注解处理工具，其性能非常好，是一种很有前途的DI方案；SpringFramework历史非常悠久，有自己的一套依赖注入体系, 依赖于Spring强大的生态是目前用的最广泛的依赖注入框架，目前已经兼容JSR330规范。

此外，基本所有的IOC框架都支持构造器注入、setter注入以及字段注入三种方式。

## 3.1.1 JSR330

如果要使用JSR330提供的注解等功能。可引入依赖:
   
``` 
<dependency>
  <groupId>javax.inject</groupId>
  <artifactId>javax.inject</artifactId>
  <version>1</version>
</dependency>
```

主要提供了以下几个注解和类：

1. @Inject

    注解@Inject标识了可注入的构造器、方法或字段。可以用于静态或实例成员。一个可注入的成员可以被任何访问修饰符（private、package-private、protected、public）修饰。注入顺序为构造器、字段、方法。超类的字段、方法将优先于子类的字段、方法被注入。对于同一个类的字段是不区分注入顺序的，同一个类的方法亦同。如：
 
    ```   
    public class IOCTest {
    
       private IUser user;
    
       @Inject
       public void setUser(IUser user) {
           this.user= user;
       }
    }
    ```

1. @Qualifier

    @Qualifier是一个元注解，用来构建自定义限定符，任何人都可以定义新的限定器注解。一个限定器注解如下：

    - 是被@Qualifier、@Retention（RUNTIME）标注的，通常也被@Documented标注。    
    - 可以拥有属性。    
    - 可能是公共 API 的一部分，就像依赖类型一样，而不像类型实现那样不作为公共 API 的一部分。    
    - 如果标注了 @Target 可能会有一些用法限制。本规范只是指定了限定器注解可以被使用在字段和参数上，但一些注入器配置可能使用限定器注解在其他一些地方（例如方法或类）上。

1. @Named

    @Named就是使用上面讲的Qualifier的一个限定器注解。可以指定依赖的组件的名称。
    
    ```
    public class IOCTest {
    
         private IUser user;
           
         @Inject
         public void setUser(@Named("adminUser") IUser user) {
             this.user= user;
         }
    }
    ```
        
    此外，@Named还可以用做标注一个组件。如
    
    ```
    @Named
    public class AdminUser implements IUser{
       public void say(){
           System.out.println("I'm admin").
       }
    }
    ```
        
1. Provider<T>

    接口Provider用于提供类型T的实列。Provider一般情况是由注入器实现的。对于任何可注入的T而言，都可以注入 Provider<T>。与直接注入T相比，注入 Provider<T> 使得：
  
    - 可以返回多个实例。    
    - 实例的返回可以延迟化或可选    
    - 打破循环依赖。    
    - 可以在一个已知作用域的实例内查询一个更小作用域内的实例。
    
    ```
    public class IOCTest {
    
      private IUser user;
        
      @Inject
      public void setUser(Provider<IUser> user) {
          this.user= user;
      }
    }
    ```
        
1. @Scope

    注解 @Scope是一个元注解，用于标识作用域注解。一个作用域注解是被标识在包含一个可注入构造器的类上的，用于控制该类型的实例如何被注入器重用。缺省情况下，如果没有标识作用域注解，注入器将为每一次注入都创建（通过注入类型的构造器）新实例，并不重用已有实例。如果多个线程都能够访问一个作用域内的实例，该实例实现应该是线程安全的。作用域实现由注入器完成。
    
1. @Singleton 

    @Singleton是基于Scope注解实现的一个作用域注解。表示注入器只实例化一次的类型。该注解不能被继承。如：
    
    ```
    @Singleton 
    public class AdminUser implements IUser{
        public void say(){
            System.out.println("I'm admin").
        }
    }
    ```

## 3.1.2 Guice

Guice是Google开源的轻量级ioc框架，兼容JSR330规范。其用 Module 來定义所有元件的实际类别。依赖定义部分可以使用JSR330的注解。

```
public class IOCTest {

  private IUser user;
    
  @Inject
  public void setUser(IUser user) {
      this.user= user;
  }
}

public class IOCTestModule extends AbstractModule {
   @Override 
   protected void configure() {
       bind(IUser.class).to(AdminUser.class);
       bind(IOCTest.class).to(IOCTest.class);
   }
}

public static void main(String[] args) {
   Injector injector = Guice.createInjector(new IOCTestModule());
   IOCTest test = injector.getInstance(IOCTest.class);
   ...
}
```

## 3.1.3 PicoContainer

PicoContainer是一个“微核心”（micro-kernel）的容器,它利用了Inversion of Control模式和Template Method模式，提供面向组件的开发、运行环境。PicoContainer是“极小”的容器，只提供了最基本的特性。其最重要的特性是实例化任意对象。这些通过它的API完成，这些API类似于HashMap。向PicoContainer指定java.lang.Class对象，之后能够获得对象实例。如：

```
MutablePicoContainer pico = new DefaultPicoContainer();  

pico.addComponent(AdminUser.class); //通过class注册 
pico.addComponent(new AdminUser()) //通过type注册
pico.addComponent(IOCTest.class); 
    
IUser user = (IUser) pico.getComponent(AdminUser.class);  
IOCTest test = (IOCTest)pico.getComponent(IOCTest.class); 
``` 
    
不过目前PicoContainer的发展几乎已经停滞。笔者仅仅在Intellij的插件开发中见过对它的使用。

## 3.1.4 Dagger

Dagger是Google开源的一个框架（早先的版本是由Square创建的，现版本由Google维护），支持Android和Java，现在已经更新到2.0。它使用生成代码实现完整依赖注入的框架（在编译期），极大减少了使用者的编码负担，且其相对于其他大部分IOC框架来说没有使用反射，性能有一定的提升。相对于都出自Google的Guice来说，其更加轻量级，没有Guice中一些相对高级的功能功，如AOP等。

Dagger的依赖注入有自己的一些注解配置，如下：

```
public class IOCTest {

  private IUser user;
    
  @Inject
  public void setUser(IUser user) {
      this.user= user;
  }
}
    
@Module
public class TestModule {   
   @Provides IUser provideUser() {       
       return new AdminUser();    
   }
}
    
@Component(modules = TestModule.class)
public interface TestComponent {    
   void inject(IOCTest test);
}
```
    
使用如下：

```
IOCTest test = new IOCTest();
TestComponent component = DaggerActivityComponent.builder().activityModule(new TestModule()).build();                
component.inject(test);
test.test();
...
```
    
## 3.1.5 Spring Framework

Spring的IOC应该是Java开发中最为常用的功能之一。其XML配置的一个例子如下：

```
<!--applicationContext.xml-->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:c="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="IOCTest" id="iocTest">
        <property name="user" ref="realUser"/>
    </bean>
    <bean class="AdminUser" id="realUser" />
</beans>
```

使用代码：

```
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xm");
IOCTest iocTest = context.getBean(IOCTest.class);
iocTes.test();
```

此外，从Spring2.0开始引入了对注解的支持，并且后来逐步兼容了JSR330规范。这里简单对比以下Spring自身的依赖注入注解与JSR330。

Spring | Jsr330 | 备注
----|-----|------|----
@Autowired | @Inject | @Inject注解没有required属性
@Component	| @Named  | JSR_330标准并没有提供复合的模型，只有一种方式来识别组件
@Scope(“singleton”)	| @Singleton	| JSR-330默认的作用域类似Spring的prototype，而Spring默认是单例的。如果要使用非单例的作用域，开发者应该使用Spring的@Scope注解。java.inject也提供一个@Scope注解，然而，这个注解仅仅可以用来创建自定义的作用域时才能使用。
@Qualifier	| @Qualifier/@Named	 | javax.inject.Qualifier仅仅是一个元注解，用来构建自定义限定符的。而String的@Qualifier等限定符可以通过javax.inject.Named来实现

除上述的注解外，Spring还有注入@Value、@Required等注解，这在JSR330中都没有对应的东西。

这里需要补充的一点是，Spring的IOC目前早已支持JSR250（common annotations）中提供的注解：Resource、PostConstruct、PreDestroy。这里和Spring自带的注解做一下对比。

Spring | JSR250 | 备注
----|-----|------|----
@Autowired | @Resource | @Resource是先根据Bean的名称去匹配Bean，获取不到的话再根据类型去匹配；而@Autowired则是根据类型匹配，通过名称则需要Spring的@Qualifier注解的配合。
@PostContruct	| init-method  | Spring中的XML配置中的init-method可以有同样的作用，即在Bean构造完后做一些初始化动作。@PostContruct具有更高优先级，同时存在的话会先执行。 
@PreDesroy	| destroy-method	| Spring中的XML配置中的destroy-method可以有同样的作用，即在Bean销毁前做一些收尾工作。@PreDesroy注解具有更高优先级，同时存在的话会先执行。

这里需要说明的是，Spring对JSR250的支持的实现是在org.springframework.context.annotation.CommonAnnotationBeanPostProcessor此类中。

## 3.1.6 循环依赖

IOC中一个常见的问题就是循环依赖，如下：

```
class TestA {
   @Inject TestB b;
}
    
class TestB {
   @Inject TestC c;
}
    
class TestC {
   @Inject TestA a;
} 
```
    
以上几个IOC框架，对于循环的处理：

- 通过替换为Provider<T>，并且在构造器或者方法中调用Provider的get方法来打破循环依赖
- 如果不依赖于Provider，对于构造器中的循环依赖是无法解决的，会抛出异常。
- 对于方法或字段注入的情况，将其依赖的一边放置到单例作用域中（可以缓存），可以使得循环依赖能够被注入器解析。

