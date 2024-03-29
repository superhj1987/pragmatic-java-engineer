# 第七章 Java编程进阶

根据网络可以找到的资料以及笔者能够打听到的消息，目前国内外著名的几个大型互联网公司的主要语言选型如下：

1. Google: C/C++、Go、Python、Java、JavaScript，不得不提的是Google贡献给Java社区的Guava包质量非常高，非常值得学习和使用。
1. Youtube、豆瓣: Python。
1. Fackbook、Yahoo、Flickr、新浪：PHP（优化过的PHP VM）。
1. 网易、阿里、搜狐: Java、PHP、Node.js。
1. Twitter: Ruby -> Java,之所以如此就在于与JVM相比，Ruby的runtime是非常慢的。并且Ruby的应用比起Java还是比较小众的。不过最近Twitter有往Scala上迁移的趋势。

可见，虽然最近这些年很多言论都号称Java已死或者不久即死，但是Java的语言应用占有率一直居高不下。与高性能的C/C++相比，Java具有GC机制，并且没有那让人望而生畏的指针，上手门槛相对较低；而与上手成本更低的PHP、Ruby等脚本语言来说，又比这些脚本语言有性能上的优势（暂且忽略FB自己开发的HHVM）。而且，Java也在不断的吸收其他语言的优势，优化自身的实现和使用。如果说Java编程是Java工程师最为基础的技能点，那么掌握其中的高级特性则是利用Java语言优势的关键。这些技能可以提高Java工程师的开发效率、代码质量以及Java应用的性能。本章就主要讲述相关知识：

 - Java内存管理：了解Java是如何做内存管理的才能从根本上掌握Java的编程技巧，避免一些内存问题的出现。
 - Java网络编程：了解网络编程模型有助于使用Java做网络编程并能够更好的优化实现。
 - Java并发编程：并发是提升应用性能非常关键的手段。
 - Java开发利器：了解Java中常用的工具类库，能够大大提升编程开发效率。
 - Java新版本特性：Java7、8、9带来了一些新特性提升开发效率和程序性能。

