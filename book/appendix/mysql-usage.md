# 附录C: MySQL常用命令

MySQL版本：5.5.19

## 系统命令

1. 启动MySQL
   
   ```
   mysqladmin start
   /ect/init.d/mysql start
   ```
       
2. 重启MySQL
   
   ```
	mysqladmin restart
	/ect/init.d/mysql restart
	```

3. 关闭MySQL
   
   ```
	mysqladmin shutdown
	/ect/init.d/mysql  shutdown
   ```

4. 连接本机上的MySQL
  
  	进入目录`mysql\bin`，键入命令`mysql -uroot -p`，回车后提示输入密码。使用`exit`退出MySQL。

5. 修改MySQL密码
  
  	```
	mysqladmin -u用户名 -p旧密码 password 新密码
	```
  	
  	或进入MySQL命令行后设置
  	
  	```
	set password for root=password("root");
	```

6. 增加新用户
   
    ```
	grant select on 数据库.* to 用户名@登录主机 identified by "密码";
    ```
  
    示例：增加一个用户test密码为123，让他可以在任何主机上登录，并对所有数据库有查询、插入、修改、删除的权限。以root用户连入MySQL，然后键入以下命令：

    ```
	grant select,insert,update,delete on *.* to test@"% " identified by "123";
    ```
    
7. 刷新MySQL的系统权限相关表

    ```
    flush privileges
    ```
    
    新设置用户或更改密码后需刷新MySQL的系统权限相关表
    
1. 查看MySQL支持的存储引擎

    ```
    show engines
    ```
    
1. 查看MySQL当前的默认存储引擎

    ```
    show variables like '%storage_engine%';
    ```
    
1. 查看Mysql服务器上的版本

    ```
    select version();
    ```
    
1. 查看数据库连接情况
	
	```
	show variables like '%max_connections%’; #查看最大连接数设置
	set global max_connections = 200; #设置最大连接数
	
	select * from information_schema.processlist where db=''; #查看进程/连接列表，可指定数据库。和show processlist一样
	```

1. 查看死锁信息

	```
	show engine innodb status; #LATEST DETECTED DEADLOCK这一栏即死锁信息
	```
	
1. 开启慢查询日志
	
	```
	show variables like '%slow%’; //查看慢查询日志配置
	set global slow_query_log='ON'; //开启慢查询
	set global long_query_time=4;//这只慢查询语句的耗时阈值
	```
    
1. 查询结果输出到文件

    ```
    [select query]  into outfile '[filePath]';
    
    pager cat > [filePath]; #所有查询结果都自动写入指定文件中，并前后覆盖
    
    mysql -h [host] -u [user] -p [password] -P [port] -e "[query]"  > [filePath]
    ```
    
## 数据操作
      
首先登录到MySQL中，有关操作都是在MySQL的提示符下进行，而且每个命令以分号结束。

1. 显示数据库列表
   
   ```  
	show databases;
	```

2. 显示库中的数据表
    
	```  
	show tables;
	```

3. 显示数据表的结构
   
   ``` 
	describe 表名;
	```

4. 建库
    
	```
	create database 库名;
    
	create database db_name default character set utf8 collate utf8_general_ci;
	```
    
	create database 的语法：
   
   ``` 
	create {database | schema} [if not exists] db_name  [create_specification  [, create_specification  ] ...] create_specification  :  [default] character set charset_name  | [default] collate collation_name 
	```

5. 建表
    
   ```
	create table 表名(字段设定列表)；
	```

6. 删库和删表
     	
	```
	drop database 库名;
	drop table 表名；
	```

7. 将表中记录清空
     
	```
	delete from 表名;
	truncate table 表名; #不同于delete, 不用扫描全表
	```

8. 显示表中的记录：
   
	``` 
	select * from 表名;
	```
		
1. 添加列

    ```
    alter table 数据表名 add 新列名 新列类型 default 0 comment;
    ```

1. 修改列名

    ```
	alter table 数据表名 change 原列名 新列名 新列类型;
	```
       
1. 修改列

    ```
	alter table 表名 modify column 列名 类型;
	```
        
1. 删除列

    ```
	alter table 表名 drop column 列名;
	```
		
1. 修改表名

    ```
	rename table 旧表名 to 新表名;
	```
			
1. 添加索引

	```
	alter table table_name add index index_name (column_list);
	alter table table_name add unique index_name (column_list);
	alter table table_name add primary key (column_list);
	```
        
1. 删除索引

	```
	drop index index_name on talbe_name
	alter table table_name drop index index_name
	alter table table_name drop primary key
	```
        
1. 查看索引

	```
	show index from tblname;
	show keys from tblname;
	```

1. 复制表结构及数据到新表

	```
	create table 新表 select * from 旧表;
	```
        
1. 只复制表结构到新表

	```
	create table 新表 select * from 旧表 where 1=2;
	create table 新表 like 旧表;
	```
        
1. 复制旧表的数据到新表（假设两个表结构一样）

	```
	insert into 新表 select * from 旧表;
	```
        
1. 复制旧表的数据到新表（假设两个表结构不一样）

	```
	insert into 新表(字段1,字段2,.......) select 字段1,字段2,...... from 旧表
	```
        
1. 设置表的自增主键起始值

	```
	alter table table_name AUTO_INCREMENT = 10000;
	```

## 数据的导入导出

1. 文本数据转到数据库中
    
    文本数据应符合的格式：字段数据之间用tab键隔开，null值用空格字符来代替。例如：
    
    ```
    1	name	test	2017-1-1
    ```
    	
	数据传入命令  
   
   ``` 
	load data local infile "文件名" into table 表名;
	```

2. 导出数据库和表
    
    将数据库news中的所有表备份到news.sql文件，news.sql是一个文本文件，文件名任取。

	```
	mysqldump --opt news > news.sql  
	```
	  	
	将数据库news中的author表和article表备份到author.article.sql文件，author.article.sql是一个文本文件，文件名任取。
   
   ``` 	
	mysqldump --opt news author article > author.article.sql
	```
 
	将数据库dbl和db2备份到news.sql文件，news.sql是一个文本文件，文件名任取。
   
   ``` 	
	mysqldump --databases db1 db2 > news.sql
	```
    
	把host上的以用户user、密码pass的数据库dbname导入到文件file.dump中
     	
 	```
 	mysqldump -h host -u user -p pass --databases dbname > file.dump
 	```
    	
	将所有数据库备份到all-databases.sql文件，all-databases.sql是一个文本文件，文件名任取。
    
	```
	mysqldump --all-databases > all-databases.sql
	```

3. 导入数据
    
    导入数据库:
   
    ``` 	
    mysql < all-databases.sql
    ```
    	
	在mysql命令行导入表:
    
	```
	source news.sql;
	```
    	
## 编码操作

- 查看数据库编码

	```
	show create database db_name;
	```
			
- 查看数据表编码，包括表使用的数据库引擎

	```
	show create table tbl_name;
	```
			
- 查看字段编码

	```
	show full columns from tbl_name;
	```

- 改变整个MySQL的编码, 启动MySQL的时候，mysqld_safe命令行加入 

	```
	--default-character-set=gbk;
	```

- 改变某个库的编码，在MySQL提示符后输入命令 

	```
	alter database db_name default character set gbk;
	```
		
- 把表默认的字符集和所有字符列（char,varchar,text）改为新的字符集
	
	```
	alter table tbl_name convert to character set character_name [collate ...];
	```
	
	示例如下：
	
	```
	alter table logtest convert to character set utf8 collate utf8_general_ci;
		
	alter table table_name convert to character set utf8mb4 collate utf8mb4_bin; #使得数据库支持emoji
	```
		
- 修改表的默认字符集
	
	```
	alter table tbl_name default character set character_name [collate...];
	```
		
- 修改字段的字符集
	
	```
	alter table tbl_name change c_name c_name character set character_name [collate ...];
	```
		
## 数据库元信息查询

information_schema数据库中保存了各个数据库以及表的元信息，主要包括：

- schemata表：提供了当前MySQL实例中所有数据库的信息。是`show databases`的结果来源。
- tables表：提供了关于数据库中的表的信息，包括视图。详细表述了某个表属于哪个schema、表的类型、表使用的引擎以及创建时间等信息。是`show tables from [schemaName]`和`show table status from [schemaName] like '[tableName]'`的结果来源。
- columns表：提供了表中的列信息。详细表述了某张表的所有列以及每个列的信息。是`show columns from [tableName]`的结果来源。
- statistics表：提供了关于表索引的信息。是`show index from [tableName]`的结果来源。
- user_privileges表：给出了关于用户权限的信息。该信息源自mysql.user授权表。
- schema_privileges表：给出了关于方案（数据库）权限的信息。该信息来自mysql.db授权表。
- table_privileges表：给出了关于表权限的信息。该信息源自mysql.tables_priv授权表。
- column_privileges表：给出了关于列权限的信息。该信息源自mysql.columns_priv授权表。
- table_constraints表：描述了存在约束的表，以及表的约束类型。

例如，可通过tables查询某个数据表的创建时间:

```
select create_time from tables where table_schema='数据库名' and table_name='表名';
```
## 数据库性能信息查询

performance_schema数据库用于收集数据库服务器性能参数，其中所有表的存储引擎为performance_schema。此功能MySQL5.5是默认关闭的，从5.6版本后变为默认开启。此功能开关如下：

```
[mysqld]
performance_schema=ON/OFF
```

其中的常用数据表如下：

- setup_actors：配置用户纬度的监控，默认监控所有用户。
- setup_consumers：配置events的消费者类型，即收集的events写入到哪些统计表中。
- setup_objects：配置监控对象，默认对mysql，performance_schema和information_schema中的表都不监控，而其它DB的所有表都监控。
- setup_timers：配置每种类型指令的统计时间单位。MICROSECOND表示统计单位是微妙，CYCLE表示统计单位是时钟周期，时间度量与CPU的主频有关，NANOSECOND表示统计单位是纳秒。但无论采用哪种度量单位，最终统计表中统计的时间都会转换到皮秒。（1秒＝1000000000000皮秒）
- file_instances：文件实例, 记录了系统中打开了文件的对象，包括ibdata文件，redo文件，binlog文件，用户的表文件等。
- rwlock_instances： 读写锁同步对象实例，记录了系统中使用读写锁对象的所有记录，其中name为 wait/synch/rwlock/*。
- socket_instances：活跃会话对象实例，记录了thread_id,socket_id,ip和port，其它表可以通过thread_id与socket_instance进行关联，获取IP-PORT信息，能够与应用对接起来。
- - socket_summary_by_instance、socket_summary_by_event_name：socket聚合统计表。
- events_waits_current：记录了当前线程等待的事件。
- events_waits_history：记录了每个线程最近等待的10个事件。
- events_waits_history_long：记录了最近所有线程产生的10000个事件。
- events_stages_current：记录了当前线程所处的执行阶段。同events_waits_current一样，events_stages_history、events_stages_history_long是历史记录。
- events_statements_current：通过 thread_id+event_id可以唯一确定一条记录。Statments表只记录最顶层的请求，SQL语句或是COMMAND，每条语句一行。event_name形式为statement/sql/*，或statement/com/*。同events_waits_current一样，events_statements_history、events_statements_history_long是历史记录。
- events_waits_summary_by_thread_by_event_name：按每个线程和事件来统计，thread_id+event_name唯一确定一条记录。包括总的等待时间、最小等待时间、平均等待时间以及最大等待时间。
- table_lock_waits_summary_by_table：聚合了表锁等待事件，包括internal lock 和 external lock。
- table_io_waits_summary_by_table：根据wait/io/table/sql/handler，聚合每个表的I/O操作（逻辑IO纬度）。
- users：记录用户连接数信息。
- hosts：记录了主机连接数信息。
- accounts：记录了用户主机连接数信息。
- threads：监视服务端的当前运行的线程。

几个常用应用示例如下：

- 统计哪个SQL执行最多

    ```
    SELECT SCHEMA_NAME,DIGEST_TEXT,COUNT_STAR,SUM_ROWS_SENT,SUM_ROWS_EXAMINED,FIRST_SEEN,LAST_SEEN FROM events_statements_summary_by_digest ORDER BY COUNT_STAR desc LIMIT 1\G
    ```
    
- 哪个SQL平均响应时间最长

    ```
    SELECT SCHEMA_NAME,DIGEST_TEXT,COUNT_STAR,AVG_TIMER_WAIT,SUM_ROWS_SENT,SUM_ROWS_EXAMINED,FIRST_SEEN,LAST_SEEN FROM events_statements_summary_by_digest ORDER BY AVG_TIMER_WAIT desc LIMIT 1\G
    ```
    
- 哪个索引没有使用过

    ```
    SELECT OBJECT_SCHEMA, OBJECT_NAME, INDEX_NAME FROM table_io_waits_summary_by_index_usage WHERE INDEX_NAME IS NOT NULL AND COUNT_STAR = 0 AND OBJECT_SCHEMA <> 'mysql' ORDER BY OBJECT_SCHEMA,OBJECT_NAME;
    ```
    
需要注意的是，MySQL5.5开启此功能即使没有数据可收集，也会有性能损失，慎重开启。MySQL5.6后做了改善，直到收集信息才会激活。


