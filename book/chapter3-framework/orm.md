# 3.2 对象关系映射

ORM（Object Relational Mapping），对象关系映射，是一种为了解决面向对象与关系型数据库不匹配而出现的技术，使开发者能够用面向对象的方式使用关系型数据库。

目前最为常用的ORM框架主要是MyBatis（前身是ibatis）和Hibernate。两者对比如下：

1. MyBatis非常简单易学，Hibernate相对较复杂，门槛较高。
1. 相比起Hibernate，MyBatis灵活性更好。
1. 系统数据处理量巨大，性能要求极为苛刻的情况下，MyBatis能够高度定制化SQL，因此会有更好的可控性和表现。
1. MyBatis需要手写SQL语句，也可以生成一部分，Hibernate则基本上可以自动生成，偶尔会写一些HQL。同样的需求,MyBatis的工作量比Hibernate要大很多。类似的，如果涉及到数据库字段的修改，Hibernate修改的地方很少，而MyBatis要把那些SQL mapping的地方一一修改。
1. 以数据库字段一一对应映射得到的PO和Hibernte这种对象化映射得到的PO是截然不同的，本质区别在于这种PO是扁平化的，不像Hibernate映射的PO是可以表达立体的对象继承，聚合等等关系的，这将会直接影响到你的整个软件系统的设计思路。

Hibernate现在主要用在传统企业应用的开发，互联网领域中由于流量大、并发高的缘故因此主要以MyBatis为主。笔者也推荐使用MyBatis做为ORM框架。

一般的ORM包括以下几个部分： 　　

- 一个规定Mapping Metadata的工具, 即数据库中的表、列与对象以及对象属性的映射。 　　
- 一个对持久类对象进行CRUD操作的API。
- 一个语言或API用来规定与类和类属性相关的查询。 　　
- 一种技术可以让ORM的实现同事务对象一起进行缓存、延迟加载等操作。

## 3.2.1 Mapping

MyBatis支持XML配置：

```
<!--mybatis-config.xml-->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="TestUserMapper.xml"/>
  </mappers>
</configuration>

<!--TestUserMapper.xml-->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="me.rowkey.pje.mybatis.TestUserMapper">
  <select id="selectUser" resultType="TestUser">
    select * from test_user where id = #{id}
  </select>
</mapper>
```

其中，environment元素体中包含了事务管理和连接池的配置,能够根据不同的环境使用不同的数据库配置。这里的dataSource设置为POOLED时使用了MyBatis自己提供的数据连接池。mappers元素则是包含一组mapper映射器（这些 mapper 的 XML 文件包含了 SQL 代码和映射定义信息）。

MyBatis也支持注解映射Mapper:

```
public interface TestUserMapper {
  
  @Select("SELECT * FROM test_user WHERE id = #{id}")
  TestUser selectUser(int id);

}
```

这里需要注意的是，命名空间现在是必需的。并且MyBatis对所有的命名配置元素的解析如果是全限定名则直接用；而如果是一个简单的名称，全局唯一的话没有问题，如果有重复类则会报错。

此外，MyBatis提供了mybatis-spring这个类库用于集成Spring和MyBatis。配置如下：

```
<!-- datasoure数据源  -->
<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
    <property name="username" value="${mysql.username}"></property>
    <property name="password" value="${mysql.pwd}"></property>
    <property name="maxTotal" value="${mysql.max}"></property>
    <property name="minIdle" value="${mysql.minIdle}"></property>
    <property name="maxIdle" value="${mysql.maxIdle}"></property>
    ...
</bean>	
 
<!-- 配置sqlSessionFactory工厂 -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">  
	<property name="dataSource" ref="dataSource" />  
	<property name="configLocation" value="classpath:sqlMapConfig.xml"></property>  
</bean>

<bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
	<constructor-arg index="0"  ref="sqlSessionFactory" />
</bean>

<bean id="userDAO" class="me.rowkey.pje.mybatis.dao.UserDao">
	<property name="sqlSessionTemplate" ref="sqlSessionTemplate"></property>
</bean>
```

## 3.2.2 CRUD以及属性的查询

MyBatis的核心是SqlSessionFactory，要使用它最基本的操作API，首先获取到SqlSessionFactory,然后再去获取相应的Mapper。

```
String resource = "classpath:mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

SqlSession session = sqlSessionFactory.openSession();
TestUserMapper mapper = session.getMapper(TestUserMapper.class);
TestUser testUser = mapper.selectUser(1);
...
```

此外，在Spring中可以使用MyBatis提供的org.mybatis.spring.SqlSessionTemplate进行操作。

```
SqlSessionTemplate sqlTemplate = new SqlSessionTemplate(sqlSessionFactory);
TestUser testUser = (TestUser)this.sqlSessionTemplate.selectOne("me.rowkey.pje.mybatis.TestMapper.selecUser", uid);
```

这里需要注意的是，SqlSession是非线程安全的，而SqlSessionTemplate则是线程安全的。

MyBatis对应于CRUD有以下几个映射语句：

- insert映射插入语句
- update映射更新语句
- delete映射删除语句
- select映射查询语句

依赖于上述的四种操作，可以完成各种crud以及对属性之类的查询。MyBatis会自动把数据库查询结果注入到返回对象中。

这里需要注意的是上述的`select * from test_user where id = #{id}`，其中的#{id}是告诉MyBatis创建预处理语句属性并以它为背景设置安全的值, 对应于PreparedStatement中的?。此外，在MyBatis中还存在另一个符号$，如下：

```
<select id="selectUserByStatus" resultType="TestUser">
    select * from test_user where status = #{status} order by ${columnName}
</select>
```

\$在这里的作用只是做字符串替换，不会修改或转义字符串。因此，能使用\#的地方就不要用$，除非是像order by这种不是参数的地方。

此外，MyBatis对于insert、update、delete以及select都提供了很多选项，用来支持注入缓存、自动映射、列名和属性名的转换等优化功能。

## 3.2.3 缓存

MyBatis支持一级缓存和二级缓存：

- Mybatis默认开启一级缓存，是SqlSession级别的，Session结束那么缓存即清空。另外还支持Statement级别，即缓存只对当前的Statement有效（没什么用）。
- MyBatis的二级缓存是Mapper级别的，被多个SqlSession共享，需要手动开启。

```
<!--TestMapper.xml-->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="me.rowkey.pje.mybatis.TestMapper">
    <cache/>
    <select id="selectUser" resultType="TestUser" useCache="false" flushCache="true">
        select * from test_user where id = #{id}
    </select>
</mapper>
```

上述配置中的useCache设置为true即开启二级缓存，对应mapper中的所有select语句的结果集将会被缓存。也可以针对某个select设置useCache为false关闭二级缓存，设置flushCache为true使得数据直接flush到数据库中防止出现脏读数据。此外，二级缓存也可以配置成使用自定义的缓存实现。

```
<cache type="me.rowkey.pje.mybatis.cache.MyCustomCache"/>
```

需要注意的是MyBatis中的缓存设计初衷是针对单点应用的，都是本地缓存，现在的分布式应用慎重开启，只单纯把MyBastis作为一个ORM框架，在Service层自己实现缓存机制是更好的选择。

## 3.2.4 结果映射

MyBatis默认会自动映射查询结果，根据SQL返回的列名并在Java类中查找相同名字的属性（忽略大小写）。还可以全局配置对数据库列的LowUnderscore（a_column）命名转换为LowCamel命名（aColumn）。

```
<configuration>  
     <settings>  
          <setting name="mapUnderscoreToCamelCase" value="true" />  
     </settings>  
</configuration>  
```

除此之外，MyBatis还支持自定义映射。

```
public class UserDto{
    private long uid;
    private String userName;
    
    //getter and setters
    ....
}

<mapper namespace="me.rowkey.pje.mybatis">  
    <resultMap type="UserDto" id="userResultMap">  
       <result property="userName" column="uname"/>  
    </resultMap> 
    
    <!-- 根据uid查询用户开放信息 -->  
    <select id="getOpenUserInfo" parameterType="String" resultType="UserDto" resultMap="userResultMap"
        useCache="true" flushCache="true">  
        <![CDATA[  
            select uid,uname from test_user  
                WHERE uid = #{id}   
        ]]>    
    </select>   
</mapper>
```

上面使用的resultMap即做了自定义的映射工作，uid会自动映射, userName会使用自定义映射。

## 3.2.5 SQL语句构建器

虽然Mybatis已经做了很多封装，可以大大简化SQL编写的工作，但是很多时候在代码拼接SQL是无法避免的。如果用字符串自己进行拼接，那么各种+号、括号、引号、格式化问题会带来很多的麻烦，一不小心就会出错。针对这种状况，MyBatis提供了SQL语句构建类org.apache.ibatis.jdbc.SQL, 可以大大简化动态SQL编写的问题。

```
new SQL() {{
    SELECT("user.id, user.user_name");
    SELECT("user.sign, user.gender");
    FROM("user_account user");
    INNER_JOIN("user_base_info uinfo on user.uid = uinfo.uid");
    WHERE("user.user_name like ?");
    OR();
    WHERE("uinfo.nick_name like ?");
    ORDER_BY("user.create_time");
  }}.toString();
```

等同于

```
"SELECT user.id, user.user_name, "
"user.sign, user.gender " +
"FROM user_account user " +
"INNER JOIN user_base_info uinfo on user.uid = uinfo.uid" +
"WHERE (user.user_name like ?) " +
"OR (uinfo.nick_name like ?) " +
"ORDER BY user.create_time";
```

## 3.2.6 使用提示

1. 当几个SQL语句都包含同样的部分SQL逻辑时。可以使用<include, refid="id" />来进行复用，如：

    ```
    <sql id="getTestByStatus_fragment">
        from test_meta where status = #{status} 
    </sql>
    
    <select id='getTestList' parameterType='map' resultMap='Test'>
        select id
        <include refid="getTestByStatus_fragment"/>
        order by
        create_time desc
    </select>
    ```

2. 动态SQL中的空字符串判断

    在动态SQL中判断是否是空字符串时，MyBatis的内建机制不太好用。建议使用以下方式：
    
    ```
    <if test="param != null and param != '' ">
    ```

3. 多ResultMap复用

    一个应用中由于功能的不同经常会有多个mapper.xml，如果一个文件需要另一个文件中的resultMap的定义，可以直接引用，而不需要再重新定义一遍。如：
    
    ```
    <!--A.mapper.xml-->
    <mapper namespace="me.rowkey.pje.mybatis">
        ...
        <resultMap id="userResultMap" type="me.rowkey.pje.mybatis.UserDto"></resultMap>
    </mapper>
    
    <!--B.mapper.xml-->
    <mapper namespace="me.rowkey.pje.test">
        ...
        <select id="bSql" parameterType="map" resultMap="me.rowkey.pje.mybatis.userResultMap>
        ...
        </select>
    </mapper>
    ```


