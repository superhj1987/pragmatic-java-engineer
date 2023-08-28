# 5.3 缓存

缓存是为了弥补持久化存储服务如数据库的性能缓慢而出现的一种将数据存储在内存中，从而大大提高应用性能的服务。如缓存五分钟法则所讲：如果一个数据频繁被访问，那么就应该放内存中。这里的缓存就是一种读写效率都非常高的存储方案，能够应对高并发的访问请求，通常情况下也不需要持久化的保证。但相对其他存储来说，缓存一般是基于内存的，成本比较昂贵，因此不能滥用。

缓存可以分为：本地缓存和分布式缓存。

## 5.3.1 本地缓存

本地缓存指的是内存中的缓存机制，适用于尺寸较小、高频的读取操作、变更操作较少的存储场景。在Java开发中常用的本地缓存实现有：
    
1. ConcurrentHashMap

    这是JDK自带的线程安全map实现，适合用户全局缓存。其get、put的操作比较简单，不用赘述。如果想要实现缓存的失效、淘汰策略则需要自定义实现。
    
1. LinkedHashMap

    LinkedHashMap也是JDK的实现。其简单的用途是一个可以保持插入或者访问顺序的HashMap，但其实其配置好是可以当做LRU cache的。这里的LRU即least recently used, 指的是固定容量的缓存，当缓存满的时候，优先淘汰的是最近未被访问的数据。
    
    ```
    int cacheSize = 1000; //最大缓存1000个元素
    
    LinkedHashMap cache = new LinkedHashMap<String, String>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > cacheSize;
        }
    };
    ```
    
    需要注意的是，LinkedHashMap是非线程安全的，如果是全局使用，需要做并发控制。
    
1. Guava Cache

    Guava Cache来自于Google开源的Guava类库中，是一个实现的比较完全的本地缓存，包括缓存失效、LRU都做了支持。
    
    ```
    final int MAX_ENTRIES = 1000; //最大元素数目
    LoadingCache<String, String> cache = CacheBuilder.newBuilder()
        .maximumSize(MAX_ENTRIES)
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())//并行度
        .expireAfterWrite(2, TimeUnit.SECONDS) //写入2秒后失效
        .build(new CacheLoader<String, String>() { 
            @Override
            public String load(String key) throws Exception {
                return ...;//异步加载数据到缓存
            }
            
            @Override
            public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                return ...;
            }
        }); 
    
    //Using the cache
    String value= cache.getUnchecked("testKey");
    ```
    
    上面的load方法是第一次加载对应的key的缓存时调用的方法,重载此方法可以实现单一线程回源，而reload方法的重载，则可以在后台定时刷新数据的过程中，依然使用旧数据响应请求，不会造成卡顿，这里默认的实现是load方法的代理，是同步的，建议重新用异步方式实现。此外，里面并行度指的是允许并行修改的线程数，此值建议根据当前机器的CPU核数来设置。
    
    上述的例子中使用了基于maximumSize和基于时间expireAfterWrite的缓存剔除，除此之外，还可以通过：
    
    1. 基于权重的缓存剔除

        ```
        CacheBuilder.newBuilder()
            .maximumWeight(10000)             
            .weigher(new Weigher<String, Object>() {  
                @Override  
                public int weigh(String key, Object value) {  
                    return key.length();  
                }  
            })
            .build();
        ```
        
        这样当cache中put一个key时，都会计算它的weight值并累加，当达到maximumWeight阀值时，会触发剔除操作。
    
    1. 制定key和value使用的引用类型来做缓存剔除
    
        ```
        CacheBuilder.newBuilder().weakKeys();
        CacheBuilder.newBuilder().weakValues();
        CacheBuilder.newBuilder().softValues();
        ```
    
    还需要指明的一点是，Guava Cache中的缓存失效并非立即生效的，通常是延迟的, 在各种写入数据时都去检查并cleanUp。
    
    此外，Guava Cache还提供了asMap视图，可以获取保存数据使用的ConcurrentMap形式。使用此视图时需要注意读写操作会重置相关缓存项的访问时间，包括asMap().get()方法和Cache.asMap().put()方法，但asMap().containsKey()方法和遍历asMap().entrySet()除外。
    
    这里还需要提到的一点是，缓存框架Caffeine使用Java8对Guava进行了重写，包括驱逐策略、过期策略和并发机制，使得缓存性能得到了显著提升，并且使用上可以兼容Guava的API。如果是在Java8上的开发，推荐直接使用Caffeine作为本地缓存实现。
    
    ```
    LoadingCache<String, String> cache = CaffeinatedGuava.build(
               Caffeine.newBuilder().maximumSize(MAX_ENTRIES),
               new CacheLoader<String, String>() { // Guava's CacheLoader
                   @Override
                   public String load(String key) throws Exception {
                       return "";
                   }
               });
    ```
    
1. Ehcache

    Ehcache是一个纯Java的进程内缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider，使用比较广泛，支持多级存储，可以将数据存储到磁盘上。其最新版本为3.x，但使用不多，且兼容性也不好，推荐使用其2.x版本即可。
    
## 5.3.2 分布式缓存

分布式缓存指的是单独的缓存服务，独立部署，通过协议、接口等提供缓存服务。相比起本地缓存，能够支持更大的容量。

几年前最流行的分布式缓存软件是Memcached，但其支持的数据结构太少，现在已经基本被Redis所取代。Redis能够支持丰富的数据结构，基于事件驱动的单线程非阻塞IO也能够应对高并发的业务场景。这里主要针对Redis来讲述，Redis版本为3.2.10。

Redis是非常强大的，既可以作为数据库又可以作为缓存，还能当做队列。总体概括来讲，其有以下用途：

1. 最简单的String,可以作为Memcached的替代品，用作缓存系统。
1. 使用SetNx可以实现简单的分布式锁(如果需要对锁设置失效期，建议使用SET key value [EX|PX] NX xx命令以保证原子性),也可参考Redis作者的RedLock算法实现分布式锁（http://redis.cn/topics/distlock.html）。
1. 使用List的pop和push功能可以作为阻塞队列/非阻塞队列。
1. 使用SUBSCRIBE和PUBLISH可以实现发布订阅模型。
1. 对数据进行实时分析，如可以累加统计等。
1. 使用Set做去重的计数统计。
1. 使用SortedSet可以做排行榜等排序场景。
1. 使用getbit、setbit、bitcount做大数据量的去重统计，允许误差的情况下可使用HyperLogLog。
1. 使用GEO可以实现位置定位、附近的人。

以上场景基本上涵盖了Redis支持的各种存储结构：

- Key: 可以是任意类型，但最终都会存储为byte[]。
- String: 简单的(key,value)存储结构，支持数据的自增、支持BitSet结构。
- Hash：哈希表数据结构，支持对field的自增等操作。
- List：列表，支持按照索引、索引范围获取元素以及pop、push等堆栈操作。
- Set：集合，去重的列表。
- SortedSet：有序集合。
- HyperLogLog：可对大数据进行去重，有一定的误差率。
- GEO：地理位置的存储结构，支持GEOHASH。

### 内存压缩

Redis的存储是以内存为主的，因此如何节省内存是使用的时候一个非常关键的地方。毕竟一个String类型的存储即使key和value是简单的1字节，其占用空间也达到了差不多64字节（估算近似值，包括了dictEntry、redisObject、key、value以及内存对齐等）。

首先，key越短越好，可以采取编码或者简写的方式。如用户的笔记数目缓存key可以使用u:{uid}:n_count作为Key。同时,key的数量也要控制，可以考虑使用hash做二级存储来合并类似的key从而减少key的数量。

其次，value也是越小越好，尤其是存储序列化后的字节时，要选择最节省内存的序列化方式, 如Kryo、Protobuf等。

此外，Redis支持的数据结构的底层实现会对内存使用有很大的影响，如：缓存用户的头像时，可以根据用户ID做分段存储，每一段使用hash结构进行存储:
    
```
//第一段 1-999
hset u:avatar:1 1 http://xxxx
hset u:avatar:1 2 http://xxxx

//第二段 1000-1999
hset u:avatar:2 1000 http://xxxx
hset u:avatar:2 1999 http://xxxx
```
    
这样，相比起使用String存储，hash底层会使用ziplist做存储，极大地节省内存使用。但这里需要注意的是Redis有一个hash-max-ziplist-entries的参数，默认是512，如果hash中的field数目超过此值，那么hash将不再使用ziplist存储，开始使用hashtable。但是，此值设置过大，那么在查询的时候就会变慢。从实践来看，此值设置为1000，hash分段大小也为1000，此时的修改和查询性能最佳。此外，还有一个hash-max-ziplist-value参数，默认是64字节，value的最大字符串字节大小如果大于此值，那么则不会使用ziplist。

除了hash之外，其他数据结构也有类似的内存编码变化，使用的时候也需要注意。如下所示：

数据结构 | 编码 | 条件
----|-----|------
hash| ziplist| 最大value大小 <= hash-max-ziplist-value && field个数 <= hash-max-ziplist-entries
hash| hashtable | 最大value大小 > hash-max-ziplist-value || field个数 > hash-max-ziplist-entries
list| ziplist| 最大value大小 <= list-max-ziplist-value && field个数 <= list-max-ziplist-entries
list| linkedlist| 最大value大小 > list-max-ziplist-value || 列表长度 > list-max-ziplist-entries
set| intset| 元素都为整数 && 集合长度 <= set-max-intset-entries
set| hashtable| 元素非整数类型 || 集合长度 > set-max-intset-entries
sortedSet | ziplist| 最大value大小 <= zset-max-ziplist-value && 集合长度 <= zset-max-ziplist-entries
sortedSet | skiplist | 最大value大小 > zset-max-ziplist-value || 集合长度 > zset-max-ziplist-entries

此外，对于list来说，Redis 3.2使用了新的数据结构quicklist来编码实现，废弃了list-max-ziplist-value和list-max-ziplist-entries配置，使用list-max-ziplist-size（负数表示最大占用空间或者正数表示最大压缩长度）和list-compress-depth（最大压缩深度）这俩参数进行配置。

还有一点需要注意的是内存碎片，所谓内存碎片指的是小的非连续的内存，这种内存无法得到充分使用，会造成浪费。我们可以通过info命令获取mem_fragmentation_ratio（used_memory_rss/used_memory）此值来观察内存碎片的程度。

- 此值通常在1左右，越大表示表示存在（内部或外部的）内存碎片。
- 小于1时表示Redis的部分内存被换出到了交换空间，会降低操作性能。

### Redis Lua

一般情况下，Redis提供的各种操作命令已经能够满足我们的需求。如果需要一次将多个操作请求发送到服务端，可以通过Jedis客户端的pipeline接口批量执行。但如果有以下三种需求，就需要使用Redis Lua：

- 需要保证这些命令做为一个整体的原子性。
- 这些命令之间有依赖关系、
- 业务逻辑除了Redis操作还包括其他逻辑运算。

Redis从2.6后内置对Lua Script的支持，通过eval或者evalsha执行Lua脚本。其脚本的执行具有原子性，因此适用于秒杀、签到等需要并发互斥且有一些业务逻辑的业务场景。

```
String REDIS_SCRIPT_GRAB_GIFT =
            "local giftLeft = tonumber(redis.call('get',KEYS[1])) or 0;" //读取礼物剩余数量
                    + "if(giftLeft <= 0) then return 0; end;" //抢购失败
                    + "redis.call('decr',KEYS[1]);" //减少礼物数量
                    + "return 1;";

...
Object grabResutl = jedis.eval(REDIS_SCRIPT_GRAB_GIFT, Lists.newArrayList("test:gifts:" + giftId + ":left"),null);
...
```

使用Redis Lua需要注意的是：

- Lua脚本里涉及的所有key尽量用变量，从外面传入，使Redis一开始就知道你要改变哪些key，尤其是在使用redis集群的时候。
- 建议先用SCRIPT LOAD载入script，返回哈希值。然后用EVALHASH执行脚本，可以节省脚本传输的成本。
- 如果想从Lua返回一个浮点数，应该将它作为一个字符串（比如ZSCORE命令）。因为Lua中整数和浮点数之间没有什么区别，在返回浮点数据类型时会转换为整数。

### 数据失效和淘汰

如果某些数据并不需要永远存在，可以通过Expire设置其失效时间，让其在这段时间后被删除。这里设置了失效时间之后可以通过SET 和 GETSET 命令覆写失效期或者使用PERSIST去掉失效期。需要注意的是如果一个命令只是更新一个带生存时间的 key 的值而不是用一个新的 key 值来代替它的话，那么生存时间不会被改变。如INCR、DECR、LPUSH、HSET等命令就不改变key的失效时间。此外，设置了失效期的key其ttl是大于0的，直至被删除会变为-2, 未设置失效期的key其ttl为-1。

和大部分缓存一样，过期数据并非立即被删除的。在Redis中，其采取的方式如下：

- 消极方法：主动get或set时触发失效删除
- 积极方法：后台线程周期性（每100ms一次）随机选取100个设置了有效期的key进行失效删除，如果有1/4的key失效，那么立即再选取100个设置了有效期的key进行失效删除。

这里需要注意的是当使用主从模式时，删除操作只在Master端做，在Slave端做是无效的。

此外，当对Redis设置了最大内存maxmemory, 那么当内存使用达到maxmemory后，会触发缓存淘汰。Redis支持以下几种淘汰策略：

- volatile-lru：从已设置过期时间的数据集中挑选最近最少使用的数据淘汰。
- volatile-ttl：从已设置过期时间的数据集中挑选将要过期的数据淘汰。
- volatile-random：从已设置过期时间的数据集中任意选择数据淘汰。
- allkeys-lru：从数据集中挑选最近最少使用的数据淘汰。
- allkeys-random：从数据集中任意选择数据淘汰。
- noeviction：禁止驱逐数据。

其中，volatile-lru是3.0版本之前的默认淘汰策略，之后的版本默认策略改成了noeviction。

为了配合LRU的淘汰策略，Redis的内部数据结构中有一个lru字段记录了对象最后一次被访问的时间。可以通过object idletime [key]来在不更新lru字段的情况下查看相应key的空闲时间。进一步的可以结合使用scan+object idletime [key]来查询哪些健长时间未被访问，以判定热点key和冷key。

这里需要注意的是Redis中为了节省内存占用使用了整数对象池（即共享整数对象），但当淘汰策略为LRU时，由于无法对对象池的同一个对象设置多个访问时间戳，因此不再会使用整数对象池。

### 持久化

Redis支持对内存中的数据进行持久化，包括两种实现方式：

1. RDB

    RDB是基于二进制快照的持久化方案，其在指定的时间间隔内（默认触发策略是60秒内改了1万次或300秒内改了10次或900秒内改了1次）生成数据集的时间点快照（point-in-time snapshot),从而实现持久化。基于快照的特性，使其会丢失一些数据，比较适用于对Redis的数据进行备份。此外，RDB进行时，Redis会fork()出一个子进程，并由子进程来遍历内存中的所有数据进行持久化。在数据集比较庞大时，由于fork出的子进程需要复制内存中的数据，因此这个过程会非常耗时，会造成服务器停止处理客户端，停止时间可能会长达一秒。
    
    可配置RDB对数据进行压缩存储，支持字符串的LZF算法和String形式的数字变回int形式。
    
1. AOF
    
    AOF是基于日志的持久化方案，记录服务器执行的所有写操作命令，并在服务器启动时，通过重新执行这些命令来还原数据集。这些命令全部以 Redis 协议的格式来保存（纯文本文件），新命令会被追加到文件的末尾。此外，为了避免AOF的文件体积超出保存数据集状态所需的实际大小，Redis在AOF文件过大时会fork出一个进程对AOF文件进行重写（将历史AOF记录中的命令合并替换成key-value的插入命令）。AOF这种方案，默认是每隔1秒进行一次fsync（将日志写入磁盘），因此与RDB相比，其最多丢失1秒钟的数据，当然如果配置成每次执行写入命令时 fsync（执行命令成功后进行aof，非常慢），甚至可以避免任何数据的丢失。但其文件的体积是明显大于RDB的，将日志刷到磁盘和从AOF恢复数据的过程也是慢于RDB的。
    
如果想要保证数据的安全性，建议同时开启AOF和RDB，此时由于RDB有可能丢失文件，Redis重启时会优先使用AOF进行数据恢复。

此外，可以通过save或者bgsave命令来手动触发RDB持久化，通过bgrewriteaof触发aof重写。如此可以将rdb或者aof文件传到另一个Redis结点进行数据迁移。

需要注意的是，如果通过kill -9或者Ctrl+c来关闭redis,那么RDB和AOF都不会触发，会造成数据丢失，建议使用redis-cli shutdown或者kill优雅关闭Redis。    

### 分布式

Redis对分布式的支持有三种：

1. Master-Slave

    简单的主从模式，通过执行slaveof命令来启动，一旦执行， Slave会清掉自己的所有数据，同时Master会bgsave出一个RDB文件并以Client的方式连接Slave发送写命令给Slave传输数据（多个slave连接时，只要在master的bgsave完成之前，那么就不会多次bgsave）。2.8版本后，Redis提供了PSYNC协议，支持主备间的增量同步，类似于断点续传，不会每次连接Master都全量同步数据。

    Redis提供了Redis Sentinel做上述方案的fail-over，能够对 Redis 主从复制进行监控，并实现主挂掉之后的自动故障转移。
    
    首先，Sentinel会在Master上建一个pub/sub channel，通告各种信息。所有Sentinel通过接收pub/sub channel上的+sentinel的信息发现彼此（Sentinel每5秒会发送一次__sentinel__:hello消息)。然后，Seneinel每秒钟会对所有Master、Slave和其他Sentinel执行ping，这些redis-server会响应+PONG、-LOADING或者-MASTERDOWN告知其存活状态等。如果一台Sentinel在30s中内没有收到Master的应答，会认为Master已经处于SDOWN状态同时会询问其他Sentinel此Master是否SDOWN,如果quonum台Sentinels认为Master已经SDOWN,那么认为Master是真的挂掉（ODOWN），此时会选出一个状态正常且与Master的连接没有断开太久的Slave作为新的Master。

    Redis Sentinel提供了notify脚本机制可以接受任何pub/sub消息，以便于发出故障告警等信息；提供了reconfig脚本机制在Slave开始提升成Master、所有Slave都已指向新Master、提升被终止等情况下触发对此类脚本的调用，可以实现一些自定义的配置逻辑。

1. Redis Cluster

    Redis 3.0后内置的集群方案。此方案没有中心节点的，每一个Redis实例都负责一部分slot（存储一部分key），业务应用需要通过Redis Cluster客户端程序对数据进行操作。客户端可以向任一实例发出请求，如果所需数据不在该实例中，则该实例引导客户端去对应实例读写数据。Redis Cluster的成员管理（节点名称、IP、端口、状态、角色）等，都通过节点之间两两通讯，基于Gossip协议定期交换并更新。是一种比较重的集群方案。

    Redis的集群方案除了内置的Redis Cluster之外，很多公司都采用基于代理中间件的思路做了一些实现，Twemproxy、Codis是其中用的比较多的软件。相比起官方的集群方案，其使用方式和单点Redis是一模一样的，原有的业务改动很少（个别命令会不支持），且其数据存储和分布式逻辑是分离的便于扩展和升级。

1. 客户端分片

    除了上述集群方案之外，在客户端做分片也是一种常用的Redis集群实现方式，不依赖于第三方分布式中间件，实现方法和代码都自己掌控，相比代理方式少了中间环节。但是此方式数据迁移、合并等都不够灵活，建议慎用。Jedis2.0开始就提供了ShardedJedis实现客户端分片，但实际应用并不多见。

### 使用提示

### Redis数据操作

- 不同业务共用同一Redis实例时，务必使用前缀来区分各个key，以防止key冲突覆盖。
- 尽量减少字符串频繁修改操作如append，setrange, 改为直接使用set修改字符串，可以降低预分配带来的内存浪费和内存碎片化。
- 不要在大数据量线上环境中使用keys命令，很容易造成Redis阻塞。
- 缓存的失效时间不要集中在同一时刻，会导致缓存占满内存触发内存淘汰（占用CPU）或者直接导致缓存雪崩。
- String类型在1KB（Redis官方测试）是一个吞吐量性能拐点，因此String类型的大小以1KB以内为宜（局域网环境下，1KB以内吞吐性能基本一致），最大不超过10KB。
- SortedSet中元素的score使用双精度64位浮点数，取值范围为-(2^53)到+(2^53)。更大的整数在内部用指数形式表示，因此如果为分数设置一个非常大的整数，其本质是一个近似的十进制数。
- 尽量使用mset、hmset等做批量操作，以节省网络IO消耗。此外，lpush、rpush、sadd也支持一次输入多个value，同样可以节省网络IO。但需要注意单次请求操作的数量尽量控制在500以内，从而避免慢查询。
- 使用Redis的事务命令（multi、exec、discard）, 其事务级别类似于Read Committed，即事务无法看到其他事务未提交的改动。还可以使用watch对某一个key做监控，当key对应的值被改变时，事务会被打断，能够达到CAS的效果。但需要注意的是Redis的事务和关系型数据库的事务不同，并非严格的ACID事务，仅仅能达到Isolation。
- 在Java中使用Jedis的pipeline一次执行多条互相没有依赖关系的命令可以节省网络IO的成本，但pipeline和事务不同，其只是一种批量写批量读的多命令流水线机制，Redis服务器并不保证这些命令的原子性。
- 可以使用SortedSet做范围查询，如：使用日期作为score,那么就可以根据日期来查询。此外，还可以在范围数据中进行查询，例如：IP定位库的数据一般是某一段IP范围属于哪一个城市,那么可以使用SortedSet存储每一段范围的最小IP和最大IP做为score，城市做为memeber。当给定一个IP时，根据score先找出大于这个IP的最小值，再找出小于这个IP的最大值，如果两者对应的城市相同，即完成定位，否则，无法获取到位置信息。
- 使用List做队列时，如果需要ack, 可以考虑再使用一个SortedSet，每次队列中pop出一个元素则按照访问时间将其存储到SortedSet中，消费完后进行删除。
- 控制集合键数据（list、set、zset、hash）的元素个数在5000以内，防止造成大key的查询阻塞其他请求的处理。可以使用zsan、hsan、sscan进行渐进操作或者分拆key来处理。
- 当无法避免对大集合键数据（元素非常多）进行全量读取时，可以通过搭建多个slave来提升性能，也可以使用Memcached作为Redis前面全量读取的缓存，从而利用MC的多线程实现方式以及对二进制KV的高效读取来获得性能的提升。
- 对大集合键数据的删除避免使用del，会造成Redis阻塞。

    - hash: 通过hscan命令，每次获取一部分字段，再用hdel命令，每次删除1个字段。
    - list： 使用ltrim命令每次删除少量元素。
    - set: 使用sscan命令，每次扫描集合中一部分元素，再用srem命令每次删除一个键。
    - zset: 使用zremrangebyrank命令,每次删除top 100个元素。
    
- 在Java开发中一般选择直接使用Jedis即可。如果需要诸如分布式锁、主从等分布式特性或者应用层级的Redis操作封装（布隆过滤器、队列），可以选择使用Redisson库来操作Redis。此外，Spring Data Redis也是一种选择，在4.2.2中做过讲述。

### 配置与监控

- 可以通过monitor命令监测Redis上命令执行的情况。
- 使用redis-cli --bigkeys可以扫描出每种数据类型最大的key。
- 由于Redis自身单线程的原因，切忌慢查询会阻塞住整个Redis, 可以通过slowlog get来查看慢查询日志。
- 设置Redis最大内存，以防内存用爆。
- 使用redis-rdb-tools对rdb文件进行分析，如每条key对应value所占的大小，从而做量化分析。
- 可以使用Redis Sampler，统计Redis中的数据分布情况。
- Redis的最大连接数默认为10000（通过命令CONFIG GET maxclients得到），可以在redis.conf配置（maxclients: 10000）。如果还是有限制，需要考虑修改系统的单个进程可打开的最大文件个数（ulimit -n）以及网络的并发连接数。
- 单点Redis的性能一般能够达到10万QPS左右。

## 5.3.3 缓存设计

在使用缓存系统的时候，还需要考虑缓存设计的问题，重点在于缓存失效时的处理和如何更新缓存。

缓存失效是在使用缓存时不得不面对的问题。在业务开发中，缓存失效由于找不到整个数据，一般会出于容错考虑，从存储层再进行查询，如果有则放入缓存。如果查找的数据压根在存储层就不存在，缓存失去意义，还给后端服务带来了巨大的请求压力，会进一步引起雪崩效应。这种现象又称为缓存穿透。

目前常用的解决缓存穿透问题的方案如下：

1. 在底层存储系统之上加一层布隆过滤器，将所有可能存在的数据哈希到一个足够大的BitMap中，一个一定不存在的数据会被这个BitMap拦截掉，从而避免了对底层存储系统的查询压力。
1. 如果数据在存储层查询也为空，那么对此空结果也进行缓存，但要设置合适的失效时间。

更进一步的，解决缓存穿透的问题其实是和缓存的更新机制是相关的。缓存更新的常用三种模式如下：

- Cache Aside Pattern: 应用程序以数据库为准，失效则从底层存储更新，更新数据先写入数据库再更新缓存。是最常用的缓存更新模式。
- Read/Write Through Pattern: 以缓存为准，应用只读写缓存，但是需要保证数据同步更新到了数据库中。
- Write Behind Caching Pattern: 以缓存为准，应用只读写缓存，数据异步更新到数据库，不保证数据正确写回，会丢数据。可以采用Write Ahead Logging等机制避免丢数据。

如上，在缓存失效时采用何种策略去更新缓存直接决定了能否解决缓存穿透的问题。Cache Aside Pattern中缓存失效则从底层存储更新无法避免缓存穿透的问题。基于以上三种模式采用下面更为细化的更新机制可以在一定程度上避免缓存穿透的问题：

1. 缓存失效时，用加锁或者队列的方式单线程/进程去更更新缓存并等待结果。
2. 缓存失效时，先使用旧值，同时异步（控制为同时只有一个线程/进程）更新缓存，缓存更新失败则抛出异常。
3. 缓存失效时，先使用旧值，同时异步（控制为同时只有一个线程/进程）更新缓存，缓存更新失败延续旧值的有效期。
4. 数据写入或者修改时，更新数据存储后再更新缓存。缓存失效时即认为数据不存在。
5. 数据写入或者修改时，只更新缓存，使用单独线程周期批量刷新缓存到底层存储。缓存失效时即认为数据不存在。此种方案不能保障数据的安全性，有可能会丢数据。
6. 采用单独线程/进程周期将数据从底层存储放到缓存中（MySQL可以基于binlog增量更新缓存）。缓存失效时即认为数据不存在。此种方案无法保证缓存数据和底层存储的数据强一致性。

如果一开始设计缓存结构的时候注意切分粒度，把缓存力度划分的细一点，那么缓存命中率相对会越高，也能在一定程度上避免缓存穿透的问题。

此外，还可以在后端做流量控制、服务降级或者动态扩展，以应对缓存穿透带来的访问压力。

