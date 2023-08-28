# 附录D: MongoDB常用命令

MongoDB版本：3.2.7

## 1. 基本操作

* db.getMongo()：取得当前服务器的连接对象。
* db.createUser(user, writeConcern) ：添加用户。
* db.dropUser(username)：删除用户。
* db.system.users.find()：查看系统所有用户列表。
* db.system.users.remove({user:"mongouser"})： 删除用户。
* db.getUsers()：查看当前数据库的用户。
* db.auth(usrename,password)；验证用户。
* db.getName()：返回当操作数据库的名称。
* db.createCollection(name)：创建一个数据集。
* db.currentOp()：查看数据库的当前操作。
* db.dropDataBase()：删除当前数据库。
* db.getCollection(collectonName)：取得一个数据集合。
* db.getCollenctionNames()：取得所有数据集合的名称列表。
* db.getLastError()：返回最后一个错误的提示消息。
* db.getLastErrorObj()：返回最后一个错误的对象。
* db.getReplicationInfo()：获得复制集的信息。
* db.printReplicationInfo()：打印复制集的信息。
* db.printCollectionStats()：返回当前库的数据集合状态。
* db.printSlaveReplicationInfo()：打印从数据库的复制集信息。
* db.printShardingStatus()：打印分片状态。
* db.commandHelp(command)：显示命令的帮助信息。
* db.runCommand(cmdObj)：运行一个数据库命令。
* db.setProfilingLevel(level，slowms)：设置数据库的优化级别(0=off,1=slow,2=all)以及慢查询的耗时阈值。
* db.getProfilingStatus()：获取数据库的优化级别和慢查询的耗时阈值。
* db.system.profile.find()：查看收集到的慢查询。
* db.version()：返回当前程序的版本信息。
* db.serverStatus().connections：连接数信息,其中current数值+available数值就是当前mongodb最大连接数。
* db.serverStatus().mem：内存占用信息。
* db.cloneDataBase(fromhost)：从目标服务器克隆一个数据库。
* db.copyDatabase(fromdb,todb,fromhost)：复制数据库:fromdb-源数据库名称，todb-目标数据库名称，fromhost-源数据库服务器地址。
* db.repairDatabase()：修复当前数据库。
* db.killOp()：停止/杀死在当前库的当前操作。
* db.shutdownServer()：安全关闭当前服务程序。

## 2. 数据集操作

* db.test_collection.drop()：删除数据集。
* db.createCollection(“test_collection”)：创建集合。
* db.test_collection.renameCollection("test_collection1")：重命名集合。
* db.test_collection.find({status:1})：返回test_collection数据集status=1的数据集。
* db.test_collection.find({status:1}}).count()：返回test_collection数据集中status=1的数据总数。
* db.test_collection.find({status:1}).limit(3)：返回test_collection数据集中status=1的前三条数据。
* db.test_collection.find({status:1}).skip(2)：返回test_collection数据集中status=1的从第三条开始的数据。
* db.test_collection.find({status:1}).limit(24).skip(8)：返回test_collection数据集中status=1的从第九条开始的24条数据。
* db.test_collection.find({status:1}}).sort()：返回test_collection数据集中status=1的有序数据。
* db.test_collection.findOne([query]) 返回符合条件的一条数据。
* db.test_collection.getIndexes()：返回此数据集的索引信息。
* db.test_collection.mapReduce(mayFunction,reduceFunction)：执行MapReduce操作。
* db.test_collection.remove(query)：在数据集中删除一条数据。
* db.test_collection.remove({})：清空数据集合。
* db.test_collection.save(obj)：往数据集中插入/更新一条数据。
* db.test_collection.stats()：返回此数据集的状态。
* db.test_collection.storageSize()：返回此数据集的存储大小
* db.test_collection.totalIndexSize()：返回此数据集的索引文件大小。
* db.test_collection.totalSize()：返回此数据集的总大小。
* db.test_collection.update(query,object[,upsert_bool])：在此数据集中更新一条数据。
* db.test_collection.createIndex(keys[,options])：创建索引。
* db.test_collection.getIndexes()：查看索引。
* db.test_collection.dropIndex('[indexName]')：删除索引。

## 3. MongoDB语法与关系型数据库SQL语法比较

* db.test_collection.find({'name':'testname'}) <-> select * from test_collection where name='testname'
* db.test_collection.find({$or:[{'name':'testname'},{'name':'testname2'}]}) <-> select * from test_collection where name='testname' or testname='testname2'
* db.test_collection.find() <-> select * from test_collection
* db.test_collection.find({'status':1}).count() <-> select count(*) from test_collection where status=1
* db.test_collection.find().skip(10).limit(20) <-> select * from test_collection limit 10,20
* db.test_collection.find({'status':{$in:[1,2]}}) <-> select * from test_collection where status in (1,2)
* db.test_collection.find().sort({'status':-1}) <-> select * from test_collection order by status desc
* db.test_collection.distinct('name',{'age':{$lt:25}}) <-> select distinct(name) from test_collection where age < 1
* db.test_collection.group({key:{'name':true},cond:{'name':'foo'},reduce:function(obj,prev){prev.msum+=obj.star;},initial:{msum:0}}) <-> select name,sum(stat) from test_collection group by name
* db.test_collection.find('this.age<25',{name:1}) <-> select name from test_collection where age < 20
* db.test_collection.insert({'name':'testname','age':25})<->insert into test_collection ('name','age') values('testname',25)
* db.test_collection.remove({}) <-> delete from test_collection
* db.test_collection.remove({'age':25}) <-> delete from test_collection where age=25
* db.test_collection.remove({'age':{$lt:20}}) <-> delete from test_collection where age<25
* db.test_collection.remove({'age':{$lte:20}}) <-> delete from test_collection where age<=25
* db.test_collection.remove({'age':{$gt:20}}) <-> delete from test_collection where age>25
* db.test_collection.remove({'age':{$gte:20}}) <-> delete from test_collection where age>=2
* db.test_collection.remove({'age':{$ne:20}}) <-> delete from test_collection where age!=25
* db.test_collection.updateMany({'name':'testname'},{$set:{'age':30}}) <-> update test_collection set age=30 where name='testname'
* db.test_collection.updateMany({'name':'testname'},{$inc:{'age':2}}) <-> update test_collection set age=age+2 where name='testname'
* db.test_collection.find({name: /testname/}) <-> select * from test_collection where name like ‘%testname%’;
* db.test_collection.find({name: /^testname/}) <-> select * from test_collection where name like ‘testname%’;

## 4. 开启安全认证

### 创建管理员用户

```
use admin
db.createUser(
    {
        user: "root",
        pwd: "root123",
        roles: [ { role: "userAdminAnyDatabase", db: "admin" } ] #roles设置为root则为超级用户权限
    }
)

mongod --auth --port 27017 --dbpath /data/db --authenticationDatabase "admin"
use admin;
db.auth("root","root123");
```

### 创建用户
```
use test_db;
db.createUser(
    {
        user: "mongouser",
        pwd: "mongo123",
        roles: [
            { role: "readWrite", db: "test_db" }
        ]
    }
)
```

### 修改密码

```
db.changeUserPassword("mongouser", "123456”)
```

### 获取某用户的权限信息

```
db.getUser("mongouser")
```

### 获取某角色的权限信息

```
db.getRole( "read", { showPrivileges: true } )
```

### 赋予权限

```
use test_db;
db.grantRolesToUser(
    "mongouser",
    [
      { role: "read", db:  “test_db" }
    ]
)
```

### 删除权限

```
use test_db;
db.revokeRolesFromUser(
    "mongouser",
    [
      { role: "readWrite", db: "test_db" }
    ]
)
```

