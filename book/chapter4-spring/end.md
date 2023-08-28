# 4.5 总结

本章讲述了Spring Core以及几个常用组件的使用。除了这些之外Spring中还有其他非常多的组件都提供了各个方面的功能封装，如：

- Spring Security: 安全、权限控制。
- Spring Retry： 重试机制的封装框架。
- Spring OAuth：OAuth实现框架。
- Spring JMS：JMS的封装。
- Spring AMQP: AMQP的使用封装框架。
- Spring Rabbit: RabbitMQ的使用封装框架。

额外地，这里对目前非常火热的Spring Cloud做一下简单介绍。
    
Spring Cloud给我们构建分布式系统提供了一整套开发工具和框架。现在很多公司和团队都是基于Spring Cloud这一套东西在做微服务实现。不过，Spring Cloud包含很多子项目，想要吃透这些得花不小的成本。

Spring Cloud的主要子项目如下：
    
- Spring Cloud Config: 统一配置中心，类似于前文说过的Disconf, 不过其配置文件是存储在版本管理系统如Git、SVN上的。其配置的实时在线更新则需要依赖Spring Cloud Bus。    
- Spring Cloud Security: 提供了OAuth2客户端的负载均衡以及认证header等安全服务，可以做为API网关的实现。
- Spring Cloud Consul/Zookeepr: 服务统一发现、注册、配置服务。类似于Dubbo。
- Spring Cloud Bus: 提供了服务之间通信的分布式消息事件总线，主要用来在集群中传播状态改变（如配置改动）。
- Spring Cloud Sleuth: 分布式跟踪系统, 能够追踪单次请求的链路轨迹以及耗时等信息。

此外，使用Spring Cloud Netflix则能够集成使用Netflix的各个组件构建服务。Netflix的主要组件如下：

- Zuul：这是Netflix所有后端服务最前端的一道门，也就是我们上面说的API网关, 主要包含了以下功能：

    - 认证授权和安全：识别合法的外部请求，拒绝非法的。
    - 监控：跟踪记录所有有意义的数据以便于给我们一个精确的产品视图。
    - 动态路由：根据需要动态把请求路由到合适的后端服务上。
    - 压力测试：渐进式的增加对集群的压力直到最大值。
    - 限流：对每一种类型的请求都限定流量，拒绝超出的请求。
    - 静态响应控制：对于某些请求直接在边缘返回而不转发到后端集群。
    - 多区域弹性：在AWS的多个region中进行请求路由。
    
- Eureka： 是Netflix的服务注册发现服务，类似于Dubbo的功能。包括负载均衡和容错。
- Hystrix：Hystrix是一个类库。基于命令模式，实现依赖服务的容错、降级、隔离等。在依赖多个第三方服务的时候非常有用。此外，还可以通过自定义实现Dubbo的filter来给Dubbo添加Hystrix的特性支持。




