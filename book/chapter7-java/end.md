# 7.6 总结

本章主要讲述了Java开发中的一些高级特性，包括内存管理、网络编程、并发编程，并介绍了常用的Java工具库以及Java7、8、9一些值得使用的新特性。

了解并掌握这些，能够使得在构建Java应用时能够使用更高级的技能，从而可以提升编码效率和编码质量。

除此之外，在平时的Java开发中，还有一些容易被忽视的点也需要大家了解：

- float和double只能用来做科学计算或者是工程计算，在商业计算中我们要用java.math.BigDecimal。但是如果使用BigDecimal(double val)此构造方法那么由于小数的double底层存储是一个不确定的数字使得构造的BigDecimal也不是一个确定的数字，应该使用BigDecimal(String val)构造方法做精确计算。
- 使用基于数组的集合时，如ArrayList、HashMap时必须指定初始化大小，否则大小不足时，会成倍扩容。
- String自带的split方法是基于正则的，尽量避免使用。
- DateFormat类以及子类是非线程安全的，在多线程环境下不能使用单例。
- 能够避免使用正则表达式的地方尽量避免使用。正则运算对CPU的消耗是非常大的，而且会在某些偶然场景下触发死循环正则运算。
- JSON的序列化和反序列化也都非常消耗CPU，除非必须得用，尽量避免使用，尤其只为了打印类的表示信息时。
- 做时间差值相关的统计时为了防止时间调整带来的影响，推荐使用System.nanoTime()而不是System.currentTimeMillis()来记录时间值。其返回的是纳秒，来源于CPU时钟。但需要注意此值仅可用于测量同一台机器的时间差值，切忌用在不同机器上。

## 学习资料

### 书籍

- [《Java核心技术(卷1)》](https://book.douban.com/subject/3146174/)：学习Java必备的黄皮书，入门推荐书籍
- [《Java核心技术(卷2)》](https://book.douban.com/subject/3360866/)：黄皮书之高级特性
- [《Java并发编程实战》](https://book.douban.com/subject/10484692/): 对Java并发库讲得非常透彻
- [《Effective Java》](https://book.douban.com/subject/3360807/)：Java之父高司令都称赞的一本Java进阶书籍
- [《写给大忙人看的Java SE 8》](https://book.douban.com/subject/26274206/):涵盖了Java8带来以及Java7中被略过的新的Java特性，值得一看

### 资料

- Socket编程: <http://ifeve.com/java-socket/>
- NIO: <http://ifeve.com/java-nio-all/>
- 序列化: <http://ifeve.com/java-io-s-objectinputstream-objectoutputstream/>
- RPC框架: <http://dubbo.io>
- 并发编程：<http://ifeve.com/java-concurrency-constructs/>

