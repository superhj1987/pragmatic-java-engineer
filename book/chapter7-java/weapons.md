# 7.4 Java开发利器

在Java开发中，有很多成熟的开源工具库供大家选用，覆盖了很多常见的但JDK中没有提供的功能和使用场景。学习并熟练使用这些类库，能让你在编码过程中少走很多弯路，并且也能学习到很多漂亮的编码方式和风格。比较常用的有以下几个：

- Apache Commons：Apache开源的Java相关工具库，囊括了编码、文本、网络等等一系列工具类。
- Guava：Google贡献的一个服务于Java6、7的类库，囊括了集合、字符串、缓存等一系列工具类。
- Joda Time: 为了弥补JDK自带日期类使用上的不方便而创造的一个日期时间工具库，已经得到了广泛运用。
- FastJson: 阿里开源的JSON序列化、反序列化类库，使用比较方便，性能也比较好。
- Orika: 简单快速高效的Java Bean映射、复制框架。
- MapDB: 专为Java设计的高性能的数据库，可以被用作多级缓存。
- FastUtils：扩充了Java的集合类，提供了很多快速、压缩、支持基本数据类型的集合类以及大规模集合。
- JCTools: 提供很多并发集合类，适用于高并发的业务场景。
- Relections：org.relections提供了一系列对于运行时元数据的查询接口，大大简化了Java自带反射API的使用。
- Lombok：一个简化开发工作的开发工具，能够节省很多重复、繁琐代码的编写。使用@Data可以省去编写getter、setter、hashCode、equals、toString等，使用@Slf4j自动生成Slf4j log对象。其原理是JDK中注解的编译时解析机制，javac会在执行的时候去调用实现了相关API（Pluggable Annotation Processing API）的程序，从而可以对编译器做一些增强。

此外，还有其他大厂出品的工具类库如：Twitter的commons、Linkedin的linkedin-utils等，基本上也都是对一些常用工具的封装。

还需要说明的是，前文提到过的Spring框架中也提供了很多可用的工具，如StringUtils、StopWatch、ReflectionUtils、ResourceUtils等，在Spring应用中完全可以拿来用。

本章主要讲述其中最为常用的Apache Commons、Google Guava、Joda Time、FastJson、Orika以及MapDB。

## 7.4.1 Apache Commons

Apache Commons是Apache开源的一个可复用Java组件库。包含了多达50个子项目。其中开发中常用的有以下几个：

- BeanUtils: 提供了对于Java Bean进行各种操作，克隆对象、属性。
- Codec: 处理常用的编码解码方法的工具类包等。
- Collections: 扩展Java集合框架的操作。
- IO: 输入输出工具的封装。
- Lang: Java基本对象（java.lang）方法的工具类包。
- HttpClient: 低层次对HTTP协议操作的封装, 提供HTTP客户端与服务器的各种通讯操作。

### BeanUtils

BeanUtils基于JDK的java.beans，提供了一系列对Java Bean的操作：读取（get）和设置（set）Bean属性值、动态定义和访问bean属性等。

先讲述一下Java Bean的定义，符合Bean规范的Java类需要符合以下要求：

- 类必须是public访问权限，且需要有一个public的无参构造方法，方便利用Java的反射动态创建对象实例。
- Bean的属性都是私有字段。
- Bean的属性值只能通过setter方法设置。
- 读取Bean的属性需要通过getter方法。

对于Bean的操作，主要是BeanUtils这个类。

BeanUtils将property分成simple（简单类型:String、int）、indexed（索引类型：数组、ArrayLsit）以及Maped（Map类型）三种类型，可以直接get和set Java Bean中的一个属性的值。
    
这里需要注意的是，此类其实最终是调用的BeanUtilsBean，但是BeanUtilsBean2继承BeanUtilsBean并做了升级，提供了任何类型到字符串的转换。因此，使用的时候建议直接使用BeanUtilsBean2。
    
```
BeanUtilsBean beanUtilsBean = new BeanUtilsBean2();
beanUtilsBean.getConvertUtils().register(false, false, 0);//错误不抛出异常、不使用Null做默认值，数组的默认大小为0
    
User user = new User();
beanUtilsBean.setProperty(user, "name", "testName");//设置属性的值
beanUtilsBean.getProperty(user,"name");//获取属性的值
```
    
这里需要注意的是，如果类型是indexed，那么属性名[index]可以直接获取某个元素的值，而对于Maped类型，属性名（key值）可以获取某一个key对应的value。
    
此外，还提供了复制以及克隆Bean的功能。
    
```
User user = new User();
user.setName("test");
User user2 = new User();
    
beanUtilsBean.copyProperties(user2,user);
User user3= (User)beanUtilsBean.cloneBean(user);
```
但这里的复制是浅复制，2个Bean的同一个属性可能拥有同一个对象的引用。

还有Map和Bean之间的转换。

```
Map<String,Object> map = beanUtilsBean.describe(user);//bean->map
beanUtilsBean.populate(user, map) //map->bean

```

此外，还有一个PropertyUtils和BeanUtils功能几乎一致。不同的是BeanUtils在对Bean赋值是会进行自动类型转化，只要属性名相同，类型会尝试转换，而PropertyUtils则会报错。

### Codec

常用的解码、编码方法封装，包括Base64、MD5、Sha1、URL。

1. Base64

    ```
    Base64.encodeBase64String(byte[] binaryData);
    Base64.decodeBase64(String base64String); 
    ```
1. MD5

    ```
    DigestUtils.md5Hex(final byte[] data);
    ```  
    
1. Sha1

    ```
    DigestUtils.sha1Hex(final byte[] data);
    ```  
1. URL

    ```
    URLCodec.encode(final String str);
    URLCodec.decode(final String str);
    ```
    
### Collections 

Collections为JDK的集合类提供了更为丰富的工具类、接口以及实现。最新的版本4，包名修改为：org.apache.commons.collections4。

1. CollectionUtils: 提供了一些方面的操作方法，如判断集合非空、对集合的并集、交集、差集的操作。

    ```
    List<String> list = getList();
    List<String> list2 = getList2();
    
    if(CollectionUtils.isNotEmpty(list)){ //判断非空
        CollectionUtils.union(list,list2)//并集
        CollectionUtils.subtract(list,list2)//差集
        CollectionUtils.retainAll(list,list2)//交集
    }
    ```

1. 提供了一些新的集合类型。

    ```
    //得到集合里按顺序存放的key之后的某一Key
    OrderedMap map = new LinkedMap();  
    map.put("1", "1");  
    map.put("2", "1");  
    map.put("3", "1");  
    map.firstKey(); // returns "1"
    map.nextKey("1"); // returns "2" 
    
    //双向map
    BidiMap bidi = new TreeBidiMap();
    bidi.put("6", "6");  
    bidi.get("6");  // returns "6"  
    bidi.getKey("6");  // returns "6"
    ```
### IO   
    
提供了一些IO工具类, 是对java.io的扩展，操作文件非常方便。

1. IOUtils：对IO stream操作的封装。

    ```
    InputStream is = new URL( "http://baidu.cim" ).openStream(); 
    try{
        IOUtils.toString(is, "utf-8");
        IOUtils.readLines(is, "utf-8");
    }finally {  
        IOUtils.closeQuietly(is);  
    }  
    ```

1. FileUtils：对文件操作的封装。

    ```
    File file = new File("/data/data.txt");  
    List lines = FileUtils.readLines(file, "UTF-8"); //读取成字符串集合
    byte[] fileBytes = FileUtils.readFileToByteArray(file); //读取成字节数组
    FileUtils.writeByteArrayToFile(file,fileBytes); //字节写入文件
    FileUtils.writeStringToFile(file, "test"); //字符串写入文件
    ```

1. FileSystemUtils：对文件系统的操作封装。
    
    ```
    FileSystemUtils.freeSpaceKb("/data"); //查看相应路径的剩余空间
    ``` 

### Lang

一些公共的工具集合，涵盖了字符串操作、字符操作、 JVM交互操作、归类、异常和位域校验等等。现在最新的版本为3, 包名改成了org.apache.commons.lang3。

1. StringUtils && StringEscapeUtils

    StringUtils继承自Object，是null safe的，即遇到null的String对象，会把它处理掉不会抛出异常；StringEscapeUtils是对字符串做转义的工具类，包括HTML、JS、XML等等。
       
    ```
    String str = ...;
    
    StringUtils.isEmpty(str); //判断字符串为空，多个连续空格不为空
    StringUtils.isBlank(str); //判断字符串为空，多个连续空格为空
    
    StringUtils.trim(str);//以strip开头的方法都是trim方法的扩展，不过可以自定义stripChars，不局限于空白符。
    
    StringUtils.equals(str,"test");//支持null
    
    StringUtils.contains("str","test"); //子字符串匹配
    
    StringUtils.split(str,";"); //根据字符/字符串分隔字符串
    StringUtils.join(new String[]{"1","2"},"-"); //根据字符连接字符串
    
    StringEscapeUtils.escapeHtml4(str); //对字符串中的html标签做转义
    ```

    这里需要注意的是其split方法，相比字符串自带的split方法使用正则，此方法直接使用了完整的字符串来做匹配，且会丢弃空字符串。
    
1. ArrayUtils

    ArrayUtils是一个对数组进行特殊处理的类。ArrayUtils扩展了JDK中的Arrays，提供了更多的功能。 
    
    ```
    String[] strs = new String[]{"1", "4", "2"};
    
    ArrayUtils.nullToEmpty(strs); //如果数组为null，则返回长度为0的数组
    ArrayUtils.reverse(strs); //反转数组
    
    ArrayUtils.addAll(strs,"3"); //数组添加元素
    ```
    
    需要注意：使用addAll添加元素，是需要数组拷贝的，慎用。
    
1.  RandomUtils && RandomStringUtils

    提供了生成随机数、字符串的操作封装。
    
    ```
    RandomUtils.nextInt(0,10); //随机一个整数，从0到10，不包括10。
    RandomStringUtils.random(3); //随机三个字母的字符串出来。
    ```
    
    这里需要注意的是这俩类都使用了Random这个类，但其是伪随机的，在要求严格的环境下，尽量不要使用这俩类，去使用SecureRandom。
    
1. NumberUtils

    为数字提供了一些操作封装, 是null safe的。
    
    ```
    String numberStr = "123";
    long n = NumberUtils.toLong(numberStr); //将字符串转化为long, 如果字符串格式不对或者为Null，则返回0，并不会抛出异常
    long max = NumberUtils.max(new Long[]{1L,5L,10L}); //计算数组最大值
    ```
1. DateUtils && DateFormatUtils

    是对日期时间操作的封装。
    
    DateUtils提供了很多日期计算。
    
    ```
    DateUtils.addDays(new Date(),3); //计算三天后的时间
    DateUtils.addHours(new Date(),3); //三个小时候的时间
    
    DateUtils.truncate(new Date(), Calendar.HOUR); //截断日期到小时，后面的分、秒都为0
    ```
    
    DateFormatUtils提供了Date到字符串表示的操作。
    
    ```
    DateFormatUtils.format(new Date(),"yyyyMMdd"); //以yyyyMMdd的格式输出日期
    ```

1. MethodUtils

    通过此工具类可以调用类的方法，实现原理基于反射。
    
    ```
    MethodUtils.invokeStaticMethod(StringUtils.class,"isNotBlank","test"); //调用静态方法
    MethodUtils.invokeMethod(user,"getName"); //调动实例方法
    ```

1. StopWatch

    StopWatch是一个秒表类。
    
    ```
    StopWatch stopWatch = new StopWatch();
    stopWatch.start(); //开始计时
    stopWatch.split(); //截断每一次的分段计时
    stopWatch.getSplitTime(); //获取分段计时
    stopWatch.suspend(); //暂停秒表
    stopWatch.resume(); //恢复计时
    stopWatch.stop(); //停止秒表
    stopWatch.getTime(); //获得总共计时
    ```
    
1. ImmutablePair && ImmutableTriple

    这俩类都是不可变的，经常是用在返回值是两个或者三个的场景下，是对多返回值的通用封装。
    
    ```
    ImmutablePair pair = ImmutablePair.of(user,user1);
    pair.getLeft();
    pair.getRight();
    ImmutableTriple triple = ImmutableTriple.of(user,user1,user2);
    triple.getLeft();
    triple.getMiddle();
    triple.getRight();
    ```
    
### HttpClient

提供HTTP客户端与服务器的各种通讯操作, 包括支持各种HTTP Method、SSL连接、Cookie、Session保持等。此工具类现在已经从Apache Commons移到Apache HttpComponents中。包名改为：org.apache.http。

1. 连接池

    HttpClient提供了HTTP连接池的支持，连接池依赖HTTP 1.1的keep alive机制，对HTTP 1.0需要做兼容配置。此外，也支持HTTPS请求。需要注意的是使用连接池能够减少频繁创建、销毁连接的消耗提高性能，但是由于连接池是有锁的，为了提升并发性能，最好对于每一个服务都创建一个连接池。

    ```
    RegistryBuilder<ConnectionSocketFactory> schemeRegistry = RegistryBuilder.create();
    schemeRegistry.register("http", PlainConnectionSocketFactory.getSocketFactory());
    
    //对https的支持
    SSLContext sslcontext = SSLContext.getInstance("TLS");
    sslcontext.init(new KeyManager[0], new TrustManager[]{new SimpleTrustManager()}, null);
    SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslcontext);
    schemeRegistry.register("https", sf);
    
    //连接池配置
    PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(schemeRegistry.build());
    pool.setMaxTotal(1000); //最大连接数支持
    pool.setDefaultMaxPerRoute(100); //每一个路由的最大连接数
    pool.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build());
    ```
1. HttpRequestBase

    HttpClient支持HTTP的各种方法, 都是HttpRequestBase的子类。
    
    - HttpGet
    - HttpPost
    - HttpPut
    - HttpDelet

    以上类都有一个参数为URL字符串的构造方法。此外，对post、put这种需要传递数据的方法，HttpClient是使用HttpEntity来实现的。常用的几个HttpEntity如下：
    
    - UrlEncodedFormEntity：最常见的表单提交，Content-type为application/x-www-form-urlencoded。
    - MultipartFormEntity: 提交文件时常用的方式，Content-type为multipart/form-data
    - StringEntiry：自包含的Entity, 传递JSON数据时可以使用。

    ```
    StringEntity entity = new StringEntity("{\"name\";\"test\"}", "UTF-8");
    entity.setContentType("application/json")    
    
    HttpPost method = new HttpPost(url);
    method.setEntity(entity);    
    ```
    
1. Cookie

    Cookie的支持需要依赖CookieStore。HttpClientUtil内置了BasicCookieStore。
    
    ```
    CookieStore cookieStore = new BasicCookieStore()
    cookieStore.addCookie(Cookie cookie);
    List<Cookie> cookies = cookieStore.getCookies();
    ```
    
1. HttpClientBuilder

    HttpClient的创建依赖于HttpClientBuilder, 能够对HttpClient的Cookie以及Connect Timeout、Socket Timout、Keep Alive的策略进行配置。
    
    ```
    HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultCookieStore(cookieStore);//cookie支持
    httpClientBuilder.setConnectionManager(pool); //设置连接池
    httpClientBuilder.setDefaultSocketConfig(pool.getDefaultSocketConfig());
    httpClientBuilder.setDefaultRequestConfig(
            RequestConfig.custom()
                    .setConnectTimeout(3000)
                    .setSocketTimeout(5000)
                    .build());
                    
    //对keep alive的策略配置
    httpClientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                            HeaderElementIterator it = new BasicHeaderElementIterator(response
                                    .headerIterator(HTTP.CONN_KEEP_ALIVE));
                            while (it.hasNext()) {
                                HeaderElement he = it.nextElement();
                                String param = he.getName();
                                String value = he.getValue();
                                if (value != null && param.equalsIgnoreCase("timeout")) {
                                    try {
                                        return Long.parseLong(value) * 1000;
                                    } catch (NumberFormatException ignore) {
                                    }
                                }
                            }
                            // 否则保持活动5秒
                            return 5 * 1000;
                        }
                    });

    HttpClient httpClient = httpClientBuilder.build()；
    ```
    
1. 使用

    ```
    method.setProtocolVersion(HttpVersion.HTTP_1_1); //设置使用http 1.1
    request.addHeader("User-Agent", agentHeader); //设置ua
    request.addHeader("Connection", "keep-alive"); //为了keepalive支持http 1.0

    HttpResponse res = httpClient.execute(request);

    byte[] byteResult = EntityUtils.toByteArray(res.getEntity());
    ```
    
    最终可以通过返回的HttResponse拿到接口返回的body、header等信息。
    
此外，Apache HttpComponents还提供了AsyncHttpClient用于异步通讯场景, 使用流程和HttpClient类似。不同之处如下：

1. 连接池管理器多了IO线程的配置且连接的Registry也不同。
    
    ```    
    Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder
          .<SchemeIOSessionStrategy>create()
          .register("http", NoopIOSessionStrategy.INSTANCE)
          .register("https", new SSLIOSessionStrategy(SSLContexts.createDefault()))
          .build();
    
    // 配置io线程
    IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
          .setIoThreadCount(Runtime.getRuntime().availableProcessors())
          .build();
    
    // 设置连接池
    ConnectingIOReactor ioReactor;
    ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
    PoolingNHttpClientConnectionManager conMgr = new PoolingNHttpClientConnectionManager(
          ioReactor, null, sessionStrategyRegistry, null);
    conMgr.setMaxTotal(100);
    conMgr.setDefaultMaxPerRoute(100);
    
    // 连接配置：忽略传输错误，默认编码使用utf-8
    ConnectionConfig connectionConfig = ConnectionConfig.custom()
          .setMalformedInputAction(CodingErrorAction.IGNORE)
          .setUnmappableInputAction(CodingErrorAction.IGNORE)
          .setCharset(Consts.UTF_8).build();
    conMgr.setDefaultConnectionConfig(connectionConfig);
    ```
    
1. 使用CloseableHttpAsyncClient。

    ```
    RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setSocketTimeout(1000).build();
    
    CloseableHttpAsyncClient asyncClient = HttpAsyncClients.custom().setConnectionManager(conMgr)
        .setDefaultCookieStore(new BasicCookieStore())
        .setDefaultRequestConfig(requestConfig)
        .build();
    ```
    
1. 使用时，需要先启动Client，并且提供了回调使用方式。
    
    ```
    asyncClient.start(); //需要先启动Client
    
    // 通过future获取结果
    HttpGet httpGet = new HttpGet("http://www.baidu.com");
    Future<HttpResponse> responseFuture = asyncClient.execute(httpGet, null);
    try {
        HttpResponse httpResponse = responseFuture.get();
        HttpEntity httpEntity = httpResponse.getEntity();
        ...
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }

    // 回调方式获取结果   
    final HttpGet httpGet2 = new HttpGet("http://www.baidu.com"); 
    asyncClient.execute(httpGet2, new FutureCallback<HttpResponse>() {
        @Override
        public void completed(HttpResponse httpResponse) {
            HttpEntity httpEntity = httpResponse.getEntity();
            ...
        }
    
        @Override
        public void failed(Exception e) {
        
        }
    
        @Override
        public void cancelled() {
        
        }
    });
    
    ...
    
    asyncClient.close();
    ```
    
    需要注意的是，无论是HttpClient还是AsyncHttpClient, 连接池都是有锁的。虽然支持对每一个route设置最大连接数，但如果是高并发场景，最好对于每一个服务都创建一个单独的HttpClient实例，使用不同的连接池。
    
    还需要提到的是，如果想要使用Http缓存提高请求的性能，可以使用Apache HttpComponents提供的httpclient-cache中的CachingHttpClient。

## 7.4.2 Guava

Guava是Google开源的一个涵盖了字符串处理、缓存、并发库、事件总线、IO等常用操作的Java核心库，也是Google自己很多Java项目依赖的工具库。其中的Guava Cache缓存已经在4.3.1讲过。

1. Preconditions

    对条件做前置判断，经常用在方法的最前面，来对参数进行校验，不符合则抛出异常。
    
    ```
    Preconditions.checkArgument(user != null,"user null error"); //user为null则抛出IllegalArgumentException
    Preconditions.checkNotNull(user); //user为null则抛出NullPointerException
    ```

1. Optional

    使用Optional<T>表示可能为null的T类型引用，能够显著地降低代码抛出NullPointerException的可能。其中其of方法和fromNullable，前者传入的值不能为空，后者则可以传入null值，表示引用缺失。提供了isPresent()方法判断是否引用缺失，在调用get之前务必先调用isPresent()。但Optional不能乱用，建议仅仅用在对外的API和接口的返回值上。
    
    ```
    String str = ...;
    Optional<String> optional = Optional.fromNullable(str);
    if(optional.isPresent()){
        String tmp = optional.get();
    }
    
    str = optional.or("default string");
    str = optional.or(new Supplier<String>() {
        @Override
        public String get() {
            return "default string";
        }
    });
    str = optional.orNull();
    
    // 对optional中的value做转换操作
    optional.transform(new Function<String, Object>() {
        @Override
        public Object apply(String input) {
            return "transformed string";
        }
    });
    ```

1. Objects && MoreObjects

    是对Java中Object的操作的扩展，后者是对前者的升级,都是null safe的。包括非空判断、相等判断、空值处理、hashcode计算等。
    
    ```
    User user = new User();
    User user1 = new User();  
    ...  
    
    Objects.equal(user,user1); //equal判断
    Objects.hashCode(user); //获取对象实例的哈希值
    MoreObjects.toStringHelper(user).add("name","testName").toString(); //辅助编写类的toString方法
    MoreObjects.firstNonNull(user,new User()); //取第一个非空的实例，可以用来在设置第一个元素为null时的默认值。
    ```
    
1. ComparisonChain

    提供了链式的比较器，执行比较操作直至发现非零的结果, 之后的比较将被忽略。
    
    ```
    ComparisonChain.start()
                .compare(user.getName(),user1.getName())
                .compare(user.getAge(),user1.getAge())
                .result();
    ```

1. Strings && Joiner && Splitter && CaseFormat

    这几个类都是对字符串的操作，包括分割、连接、格式转换等。
    
    ```
    Strings.nullToEmpty(str); //如果字符串为null，则转换为空字符串
    Strings.repeat(str,3); //重复字符串成新的字符串
    
    Joiner joiner = Joiner.on(";").skipNulls(); 
    joiner.join("1", null, "2", "3","4"); //使用;拼接字符串
    
    Splitter.on(';')
            .trimResults()
            .omitEmptyStrings()
            .split("1;2;3;4"); //分隔字符串
    
    CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "user_name"); // 将字符串从low underscore命名格式转换为low camel命名。
    ```
    
    其中的命名格式转换还支持LOWER_HYPHEN、UPPER_CAMEL以及UPPER_UNDERSCORE。

1. ImmutableList && Multiset && Multimap

    这些是Guava实现的新的集合类型。
    
    ImmutableList是不可变集合（保证线程绝对安全）的一种。除此之外，还有ImmutablesSet、ImmutableMap，用法都类似。
    
    ```
    ImmutableList<String> list = ImmutableList.of("1","2","3");
    ```
    Multiset可以多次添加相等的元素,主要统计给定元素的个数。
    
    ```
    Multiset<String> set = HashMultiset.create();
    set.add("1");
    set.add("1");
    System.out.println(set.count("1"));
    ```
    Multimap是一个key映射多值的Map，类似于`Map<K, List<V>>、Map<K, Set<V>>`。主要是使用其两个子类：ListMultimap和SetMultimap，前者允许重复值，后者则不允许。
    
    ```
    Multimap<String,String> multimap = ArrayListMultimap.create();
    multimap.put("test","1");
    multimap.put("test","2");
    multimap.get("test"); //["1","2"];
    ```
    
1. Lists && Maps

    JDK默认提供了Collections作为集合工具类。Guava针对其没有提供的集合工具操作做了扩展和实现。Lists、Maps是List和Map的工具类。最大的一个特性是提供的静态工厂方法相比先创建ArrayList再添加元素或者设置参数的初始化方式要简单优雅很多。
    
    ```
    Lists.newArrayList("1", "2", "3");
    Lists.newArrayListWithCapacity(100);
    ```
    
1. ListenableFuture

    ListenableFuture继承了JDK concurrent包下的Future接口，可以大大简化并发逻辑的编写。可以注册回调方法，在运算（多线程执行）完成的时候进行调用, 或者在运算（多线程执行）完成后立即执行。
    
    ```
    ListeningExecutorService service  = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    ListenableFuture future = service.submit(new Callable<String>() {
          @Override
          public String call() throws Exception {
              return "result";
          }
    });
    Futures.addCallback(future, new FutureCallback() {
          @Override
          public void onSuccess(Object result) {
              ...
          }
        
          @Override
          public void onFailure(Throwable t) {
            ...
          }
    });
    ```
    
    还支持链式操作：
    
    ```
    Futures.transform(future, new AsyncFunction() {
         @Override
         public ListenableFuture apply(Object input) throws Exception {
             return ...;
         }
    }, new Executor() {
         @Override
         public void execute(Runnable command) {
             
         }
    });
    ```
    
1. EventBus
    
    Guava实现的事件总线机制，为了替代通用型的发布-订阅模型，不适用于进程间通信。
    
    EventBus定义了事件生产者和事件监听者的角色，类似于生产者和消费者。
    
    - 事件生产者：管理和追踪监听者以及向监听者分发事件。
    - 事件监听者：注册到总线上，按照Event监听。只要以ChangeEvent为唯一参数创建方法，并用Subscribe注解标记即可实现一个监听者。register到EventBus上即可开始监听Event。

    ```
    class EventBusListener {
        @Subscribe
        public void recordCustomerChange(ChangeEvent e) {
            System.out.println(e.getSource());
        }
    }
    
    EventBus eventBus = new EventBus(); //还可以使用AsyncEventBus异步发送event
    eventBus.register(new EventBusListener());

    eventBus.post(new ChangeEvent("test event"));
    ```
    
1. RateLimiter

    使用令牌桶的速率限制器，经常用于限制对一些物理资源或者逻辑资源的访问速率。与JDK中的Semaphore相比，Semaphore 限制了并发访问的数量而不是使用速率。
    
    ```
    final RateLimiter rateLimiter = RateLimiter.create(2.0); //桶容量为2且每秒新增2个令牌

    Executor executo = Executors.newFixedThreadPool(10);
    
    for (Runnable task : tasks) {
            rateLimiter.acquire(); // 也许需要等待直到有令牌可用
            executor.execute(task);
        }
    }

    ```
    
    需要注意的是RateLimiter并不提供公平性的保证，没有先来先得的概念。此外，其请求的许可数不会影响到请求本身的限制，但会影响下一次请求的限制（高开销的任务下一个请求会经历额外的限制，从而来偿付高开销任务）。如：acquire(1) 和acquire(1000)的限制效果一样，但后者的下一次请求会受到额外限制。
    
此外，Guava还提供了散列、函数式风格、IO、区间、数学运算、Service框架、BloomFilter
等等许多非常有用的工具类，都大大提高了开发效率和代码质量。其中，Guava的Optional、Objects这些由于用的非常广泛，Java8都吸收并做了类似的实现。

## 7.4.3 Joda Time

JDK自带的Date、Calendar类使用起来非常麻烦，并且日期与字符串之间的转换很慢且非线程安全。Joda Time就是为了解决这些问题而创造的日期时间库，使用起来非常简单方便。和之前Apache Commons提供的DateUtils相比，如果想继续使用Java日期，可以选择DateUtils；如果想彻底改变的话就可以使用Joda Time。

1. 初始化时间

    ```
    DateTime dateTime=new DateTime(2017, 6, 21, 18, 00,0); //2017.06.21 18:00:00
    ```  
1. 输出格式化字符串

    ```
    dateTime.toString("yyyy-MM-dd");
    ```
    
1. 解析时间字符串

    ```
    DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd");         
    DateTime dateTime = DateTime.parse("2017-06-21", format);
    ```
    
1. 时间计算

    ```
    dateTime.plusDays(1) // 增加天      
               .plusYears(1)// 增加年      
               .plusMonths(1)// 增加月      
               .plusWeeks(1)// 增加星期      
               .minusMillis(1)// 减分钟      
               .minusHours(1)// 减小时      
               .minusSeconds(1);// 减秒数 
    
    DateTime.Property month = dateTime.monthOfYear();      
    month.isLeap(); //判断是否是闰月            
               
    ```
1. 与Java Date转换

    ```
    dateTime = new DateTime(new Date());
    dateTime = new DateTime(Calendar.getInstance());
    Date date = dateTime.toDate();
    ```

## 7.4.4 FastJson

FastJson是阿里巴巴开源的JSON处理器，官方测试称性能超过MappingJackson。使用起来比较简单方便。

1. 序列化

    ```
    User user = new User();
    user.setName("testUser");
    user.setGender("M");
    Strign userJson = JSON.toJSONString(user);
    ```
    
1. 反序列化

    ```
    user = JSON.parseObject(str,User.class);
    JSONObject jo = JSON.parseObject("{\"name\":\"test\"}");
    ```
    
1. 构造JSONObject以及取值

    ```
    JSONObject jo = new JSONObject();
    jo.put("name","test");
    jo.getString("name");
    jo.getString("nickName"); //返回为null, 不会抛异常
    ``` 
1. 属性名称转化

    很多时候会遇到JSON字符串中的属性和Java Bean中的属性名称不一致的情况。这时候如果直接调用，则会出错，需要做名称转换，可以使用@JSONField注解配置别名：
    
    ```
    @JSONField(name = "nick")
    private String nickName;
    ```

    此外，FastJson默认提供了JSON属性Low Underscore到Java Bean字段Low Camel命名的转化。如果想要实现序列化的时候到Low Camel的转换除了可以使用@JSONField，还可以使用SerializeConfig, 设置其PropertyNamingStrategy。同样的，ParseConfig也能设置此策略。
    
    ```
    SerializeConfig config = new SerializeConfig();
    config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    String str = JSON.toJSONString(user, config);
    System.out.println(str); // {\"nick_name\":\"testNick\"}
    ```
    PropertyNamingStrategy还支持KebabCase（短横线连接单词）、PascalCase（大写字母开头）以及CamelCase（驼峰）。
    
1. JSONPath

    FastJson1.2.0之后提供了JSONPath，方便取值，类似于XPath。主要是为了简化取值逻辑，方便嵌套取值、过滤取值、获取集合长度等。
    
    ```
    String jsonStr = "{\"name\":\"testName\",\"interests\":[\"music\",\"basketball\"]," +
                "\"notes\":[{\"title\":\"note1\",\"contentLength\":200},{\"title\":\"note2\",\"contentLength\":100}]}";
   JSONObject jsonObject1 = JSON.parseObject(jsonStr);
   System.out.println(JSONPath.eval(jsonObject1, "$.interests.size()")); //集合长度
   System.out.println(JSONPath.eval(jsonObject1, "$.interests[0]")); //集合取值
   System.out.println(JSONPath.eval(jsonObject1, "$.notes[contentLength > 100].title")); //集合过滤取值
   System.out.println(JSONPath.eval(jsonObject1, "$.notes['title']")); //只取某一个属性的值
   ``` 
   
使用FastJson时需要注意FastJson在序列化和反序列化默认是开启ASM的（安卓下不会开启）。可以通过下面的代码关闭：

```
SerializeConfig.getGlobalInstance().setAsmEnable(false); // 序列化的时候关闭ASM  
ParserConfig.getGlobalInstance().setAsmEnable(false); // 反序列化的时候关闭ASM 
```    

## 7.4.5 Orika

Orika是一个快速、高效的Java Bean映射框架，主要用于在VO、PO等各种Bean之间复制属性，并且是深复制。相比起7.4.1中提到的BeanUtils使用反射，Orika是使用代码生成进行复制的。因此其性能好于BeanUtils和Dozer（使用反射，对反射信息做了缓存）。

```
MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
MapperFacade mapper = mapperFactory.getMapperFacade();

User user = new User();
user.setName("test");
User user1 = mapper.map(user, User.class);
```

不同类之间复制，如果属性名不一致，可以通过自定义映射来复制，属性名相同的直接可以复制。

```
MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
mapperFactory.classMap(User.class, TestUser.class)
      .field("name", "testName")
      .byDefault()
      .register();

MapperFacade mapper = mapperFactory.getMapperFacade();

User user = new User();
user.setName("test");
TestUser testUser = mapper.map(user, TestUser.class);
```

### 7.4.6 MapDB

MapDB将Java中常用的Maps, Sets, Lists, Queues等其他集合做了JVM堆外（堆外内存、磁盘）的存储实现。很多时候被用作多级缓存。如下：

```
DB db = DBMaker.memoryDB().make();

HTreeMap diskCache = db.hashMap("testCache")
      .expireStoreSize(10 * 1024)
      .expireMaxSize(1000)
      .expireAfterCreate(10, TimeUnit.SECONDS)
      .createOrOpen();

HTreeMap cache = db.hashMap("testCache")
      .expireMaxSize(100)
      .expireOverflow(diskCache)
      .createOrOpen();
```

需要注意的是，最新的MapDB使用的是Kotlin语言实现其主要逻辑。

### 7.4.7 使用Hystrix做熔断

除了HystrixBadRequestException异常之外，所有从run()方法抛出的异常都算作失败，并触发降级getFallback()和断路器逻辑。
          HystrixBadRequestException用在非法参数或非系统故障异常等不应触发回退逻辑的场景。
          
请求缓存可以让(CommandKey/CommandGroup)相同的情况下,直接共享结果，降低依赖调用次数，在高并发和CacheKey碰撞率高场景下可以提升性能.

```
HystrixRequestContext context = HystrixRequestContext.initializeContext();  
```

Servlet容器中，可以直接实用Filter机制Hystrix请求上下文

信号量隔离:SEMAPHORE
  隔离本地代码或可快速返回远程调用(如memcached,redis)可以直接使用信号量隔离,降低线程隔离开销.
  
使用hystrix-javanica的Java注解