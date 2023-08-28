# 5.5 Zookeeper

ZooKeeper是Hadoop的子项目，是一个服务协调服务，旨在解决大规模分布式应用场景下服务协调同步的问题。它可以为同在一个分布式系统中的其他服务提供：分布式锁服务、命名服务、配置管理、集群管理等功能。其是CP特性的分布式系统,还经常被用做微服务治理的注册中心（Dubbo+Zookeeper）。


## 客户端

- Zookeepre Client
- Curator


## 使用

- ZK一般用来做临时数据存储介质，即时丢失也不影响业务运行，尽量不要类似数据库那样用来做持久化存储。
- 使用zookeeper时，尽量避免大量节点监控一个节点的行为: [羊群效应](https://blog.csdn.net/wk022/article/details/88129479)。

