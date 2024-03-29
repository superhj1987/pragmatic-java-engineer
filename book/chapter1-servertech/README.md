## 第一章 后端技术导言

淘宝双11、京东618，大家疯狂抢购各种降价商品，不断确认提交订单，商家不断地发出快递；使用今日头条，无数你感兴趣的内容出现在你的面前，让你能够实时掌握你最关心的消息；使用滴滴，你快速又便宜地打到了车，到达了目的地；使用大众点评，你迅速找到了周围最受欢迎的饭店，吃到了可口的美食;使用中华万年历，你实时快速地知道了明天的天气预报，是否黄道吉日，做好了一天的日程规划...诸如此类，日常生活中这些APP给大家的生活带来了翻天覆地的改变，大家的衣食住行也越来越依赖于这些APP。在这些或绚丽、或实用的APP后面到底是什么技术在支撑着它的运行呢？

从大的范围来讲，支撑这些APP的主要有前端、后端这两种技术。前端是指的用户能够直接感知到的那些东西，包括Web前端和客户端技术；而后端技术是相对于前端技术而言的，是藏在网络后面支撑网页、APP、应用软件运行的设施、软件、服务等。通过这两种技术的结合，就产生了一个稳定的互联网软件给用户提供持续稳定的服务。其中后端技术通过几台甚至成千上万台服务器给前端提供高质量的服务，在其中扮演了一个基础设施、运转轴心的角色。

相比起前端技术的各种各样以及不可预测性（比如诺基亚的Symbian在火爆了n年以后瞬间没落相应的编程也随着失去了用武之地），后端的技术还是比较稳定的，即使新的技术层出不穷，但至少旧的应用比较广泛的技术也不会突然就走下历史舞台。但是后端技术的涵盖范围实在太广，Web、大数据、高并发、数据库、数据挖掘、机器学习...任何的一个技能点单拎出来都需要花费极大的精力才能做到融汇贯通。因此这些并非一个人能够全部掌握的，即使是我们现在眼中的很多所谓全栈工程师，其实也就是掌握了一个系统前后端的基本技能而已，要说到都能做到精通，也没几个人敢妄言的。

由于前端技术和后端技术与用户接触的层次不一样，很多关注点也是不一样的。对于后端技术来讲，一般情况下需要关注的是以下几个指标：

- 可用率：能够提供正常服务的时间占线上运行时间的百分比。这个是后端服务中一个很关键的指标，很多应用都需要达到99.9%以上。这里需要注意的是系统是否可用经常是用响应时间作为衡量指标的，关注的是系统提供正常服务的效率，越低的响应时间，越低的延迟，则系统的性能越好。
- 吞吐量：指的是系统一段时间内处理任务的能力，同响应时间一样也是衡量系统性能的关键指标。并发量则是吞吐量的延伸，指的是系统同一时间能够服务的任务数，一般来说能够支撑并发量越大，那么吞吐量也就越大。
- 稳定性：也叫做鲁棒性、健壮性，即服务在异常和危险情况下保持稳定的能力。
- 容错性：在服务出现错误或者异常的时候，能够继续提供一定服务的能力，主要强调的是容许误差、故障的能力。显然，此指标会影响可用率，容错性越好，那么系统的可用率也就会越高。
- 扩展性：主要指的服务的动态扩展能力，即通过扩展（而非修改）现有系统的能力来满足需求的能力。
- 维护性：指的是监控/修正服务错误、修改服务功能的能力，主要和运维监控方面的工作相关。
- 安全性：保障系统以及用户数据安全性的能力，包括保障系统不被非法入侵、用户数据不被泄漏等。

除此之外很多书籍和资料还会有可靠性、可用性等说法，其实本质上是以上指标的另一种说法而已。一般来说后端服务的设计目标主要包括高可用、低延迟、高吞吐、高并发、可容错、可扩展、可维护、稳定、安全。后端技术也基本是围绕着这几个目标来进行的。

综上，由于后端技术覆盖范围太广以及本人知识所限，本书所说的后端技术主要指的是一些Java后端工程师能够胜任开发工作应该必备的一些技能。



