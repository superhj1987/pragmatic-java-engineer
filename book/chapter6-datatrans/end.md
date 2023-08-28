# 总结

## 学习资料

### JMS

最为经典，也比较简单的一个消息中间件规范，ActiveMQ是其一个实现。但由于自身的一些局限，不再推荐使用。

+ 大规模分布式消息中间件简介：<http://blog.csdn.net/huyiyang2010/article/details/5969944>
+ JMS Overview: <http://docs.oracle.com/javaee/6/tutorial/doc/bncdr.html>
+ Basic JMS API Concepts: <http://docs.oracle.com/javaee/6/tutorial/doc/bncdx.html>
+ The JMS API Programming Model: <http://docs.oracle.com/javaee/6/tutorial/doc/bnceh.html>
+ Creating Robust JMS Applications:<http://docs.oracle.com/javaee/6/tutorial/doc/bncfu.html>
+ Using the JMS API in Java EE Applications: <http://docs.oracle.com/javaee/6/tutorial/doc/bncgl.html>
+ Further Information about JMS: <http://docs.oracle.com/javaee/6/tutorial/doc/bncgu.html>

### RabbitMQ

RabbitMQ是AMQP（The Advanced Message Queuing Protocol）协议的实现。适用于需要事务管理、对消息丢失很敏感的应用场景。对比kafka来看，RabbitMQ更为强调消息的可靠性、事务等。通过阅读官方文档学习即可：[官方文档](http://www.rabbitmq.com/documentation.html)

### Kafka

基于日志的消息队列，首推当然是官方文档: <http://kafka.apache.org/documentation.html>

- [kafka中文教程](http://www.orchome.com/kafka/index)：比较不错的中文教程

	学习内容：

	+ 开始学习kafka
	+ 入门
	+ 接口
	+ 配置
	+ 设计
	+ 实现
	+ 什么是kafka
	+ 什么场景下使用kafka

- [kafka-study](https://github.com/superhj1987/kafka-study): 笔者在学习kafka时的一些笔记

