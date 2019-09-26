# 附录E: Java调优常用命令

系统环境：CentOS 6.5 && JDK 1.8.0_121

## 常用Shell命令

- 查看网络状况

    ```
    netstat -n | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'
    ```
	
- 使用top命令去获取进程CPU使用率；使用/proc文件查看进程所占内存。

    ```
        
    #!/bin/bash
    for i in `ps -ef | egrep -v "awk|$0" | awk '/'$1'/{print $2}'`
    do
        mymem=`cat /proc/$i/status 2> /dev/null | grep VmRSS | awk '{print $2" " $3}'`
        cpu=`top -n 1 -b |awk '/'$i'/{print $9}'`
    done
	```
		
- Core转储快照

    Core Dump是对内存的快照，可以从Core Dump中转出Heap Dump和Thread Dump。
      	
    ```
    ulimit -c unlimited （使得jvm崩溃可以生成core dump）
    	
    gcore [pid] （主动生成core dump）
    ```
    
    生成的Core Dump文件在CentOS中位于用户当前工作目录下形如core.[pid]（可以通过`echo '/home/logs/core.%p' > /proc/sys/kernel/core_pattern`修改位置），此文件可以通过gdb、jmap和jstack等进行分析，如
    
    ```
    gdb -c [core文件] $JAVA_HOME/bin/java # 进入gdb命令行后执行bt, 显示程序的堆栈信息
    
    jmap -heap $JAVA_HOME/bin/java [core文件]
    
    jstack $JAVA_HOME/bin/java [core文件]
    ```        

## 常用JDK命令

- 查看类的一些信息，如字节码的版本号、常量池等
		
    ```
    javap -verbose [className]
    ```
- 查看JVM进程
		
	```
	jps  
		
	jcmd -l
	```
- 查看进程的GC情况
	
    ```
    jstat -gcutil [pid] #显示总体情况   
		
    jstat -gc [pid] 1000 10 #每隔1秒刷新一次，一共10次
    ```
    
    jstat读取的是/tmp/hsperfdata_$username/$pid下的统计数据，不会干扰应用程序运行。如果JVM使用-Djava.tmp.dir修改了临时目录或者使用-XX:+PerfDisableSharedMem禁止了perfdata，那么jstat无法使用。
    
- 查看JVM堆内存使用状况

    ```
    jmap -heap [pid]
    ```
	
- 查看JVM永久代使用状况

    ```
    jmap -permstat [pid] #适用于Java6、7
       	
    jmap -clstats [pid] #Java8没有永久代，这里可以打印类加载器的状况
    ```
    
- 查看JVM内存存活的对象
	
	```
	jcmd [pid] GC.class_histogram 
	
	jmap -histo:live [pid]
	```
- 把heap里所有对象都dump下来，无论对象是死是活
	
    ```
    jmap -dump:format=b,file=xxx.hprof [pid]
    ```   

- 先做一次Full GC，再dump，只包含仍然存活的对象信息：
	
    ```
    jcmd [PID] GC.heap_dump [FILENAME]
	   
    jmap -dump:format=b,live,file=xxx.hprof [pid]
    ```  	
- 线程dump
	
    ```
    jstack [pid] #-m参数可以打印出native栈的信息，-F可以强制一个无响应的进程dump（依赖SA.attach用ptrace暴力接管进程），-l参数可以打印出锁信息,
	   
    jcmd [pid] Thread.print
	
    kill -3 [pid] (在日志文件中输出)
    ```
    
    这里在jstack（同jmap）中使用-F参数需要注意：如果命令执行过程中途用kill -9非正常退出，目标jvm进程会一直暂停在那里，可以使用kill -18重新激活进程。
    
- 查看JVM启动的参数

    ```
    jinfo -flags [pid]  #查看有效参数
	   
    jcmd [pid] VM.flags #查看所有参数
    ```
- 查看对应参数的值
	
	```
	jinfo -flag [flagName] [pid]
	```
- 启用/禁止某个参数
	 
	```
	jinfo -flag [+/-][flagName] [pid]
	```  
- 设置某个参数
	 
    ```
    jinfo -flag [flagName=value] [pid]
    ```  
- 查看所有可以设置的参数以及其默认值
    
    ```
    java -XX:+PrintFlagsInitial
    ```
    
- 进行一次Full GC

    ```
    jcmd [pid] GC.run
    ```

## JVM配置示例

```
-server #64位机器下默认
-Xms6000M #最小堆大小
-Xmx6000M #最大堆大小
#-XX:+AggressiveHeap #一些激进的堆配置策略，包括将Xms和Xmx值设置为相同的值等，由于隐藏了很多调优工作，不建议启用
-Xmn500M #新生代大小
-Xss256K #栈大小
-XX:PermSize=500M #永久代大小（JDK7）
-XX:MaxPermSize=500M （JDK7）
#-XX:MetaspaceSize=128m  #元空间大小（JDK8）
#-XX:MaxMetaspaceSize=512m（JDK8）
-XX:SurvivorRatio=65536 #Eden区与Survivor区的比例
-XX:MaxTenuringThreshold=0 #晋升到老年代需要的存活次数,设置为0时，Survivor区失去作用，一次Minor GC，Eden中存活的对象就会进入老年代，默认是15，使用CMS时默认是4
-Xnoclassgc #不做类的gc
#-XX:+PrintCompilation #输出JIT编译情况，慎用
-XX:+TieredCompilation #启用多层编译，JDK8默认开启
-XX:CICompilerCount=4 #编译器数目增加
-XX:-UseBiasedLocking #取消偏向锁。偏向锁会触发进入Safepoint，引起停顿，因此高并发应用建议取消偏向锁
-XX:AutoBoxCacheMax=20000 #自动装箱的缓存数量，如int默认缓存为-128~127
-Djava.security.egd=file:/dev/./urandom #替代默认的/dev/random阻塞生成因子
-XX:+AlwaysPreTouch #启动时访问并置零内存页面，大堆时效果比较好
-XX:-UseCounterDecay #禁止JIT调用计数器衰减。默认情况下，每次GC时会对调用计数器进行砍半的操作，导致有些方法一直是个温热（虽然频繁调用但一直达不到设置的热点阈值），可能永远都达不到C2编译的1万次的阀值
-XX:ParallelRefProcEnabled=true #默认为false，并行的处理Reference对象，如WeakReference
-XX:+DisableExplicitGC #此参数会影响使用堆外内存，会造成OOM，如果使用NIO,请慎重开启
#-XX:+UseParNewGC #此参数在设置了CMS后默认会启用，可以不用设置
-XX:+UseConcMarkSweepGC #使用CMS垃圾回收器
#-XX:+UseCMSCompactAtFullCollection #是否在Full GC时做一次压缩以整理碎片，默认启用
-XX:CMSFullGCsBeforeCompaction=0 #Full GC触发压缩的次数
#-XX:+CMSParallelRemarkEnabled #并行标记, 默认开启, 可以不用设置
#-XX:+CMSScavengeBeforeRemark #强制remark之前开始一次Minor GC，减少remark的暂停时间，但是在remark之后也将立即开始又一次minor GC
-XX:+UseCmsInitiatingOccupancyOnly #只根据老年代空间占用率来决定何时启动垃圾回收线程
-XX:CMSInitiatingOccupancyFraction=90 #触发Full GC的内存使用百分比
-XX:+CMSPermGenSweepingEnabled #CMS每次回收同时清理永久代中的垃圾
#-XX:CMSInitiatingPermOccupancyFraction=80 #触发永久带清理的永久代使用百分比
#-XX:+CMSClassUnloadingEnabled #如果类加载不频繁，也没有大量使用String.intern方法，不建议打开此参数，况且JDK7后String pool已经移动到了堆中。最新版本的JDK8中，此选项默认值改为了true，但只要设置了-Xnoclassgc那么此选项失效。
-XX:+PrintClassHistogram #打印堆直方图
-XX:+PrintHeapAtGC #打印GC前后的heap信息
-XX:+PrintGCDetails #以下都是为了GC日志相关参数
-XX:+PrintGCDateStamps #打印可读日期
-XX:+PrintGCApplicationStoppedTime #打印清晰的GC停顿时间外，还可以打印其他的停顿时间，比如取消偏向锁、类重定义、代码反优化等
-XX:+PrintSafepointStatistics #打印进入Safepoint的相关信息，包括是哪一个VM操作触发进入的Safepoint、各个阶段的耗时等
-XX:PrintSafepointStatisticsCount=1 #Safepoint数据被缓存的行数
-XX:+PrintTenuringDistribution #打印晋升到老年代的年龄自动调整的情况（并行垃圾回收器启用UseAdaptiveSizePolicy参数的情况下以及其他垃圾回收器也会动态调整，从最开始的MaxTenuringThreshold变成占用当前堆50%的age）
#-XX:+UseAdaptiveSizePolicy #此参数在并行回收器时是默认开启的。会根据应用运行状况做自我调整，包括MaxTenuringThreshold、Survivor区大小等，其他情况下最好不要开启
#-XX:StringTableSize #字符串常量池表大小（hashtable的buckets的数目），Java 6u30之前无法修改固定为1009，后面的版本默认为60013，可以通过此参数设置
-XX:GCTimeLimit=98 #GC占用时间超过多少抛出OutOfMemoryError
-XX:GCHeapFreeLimit=2 #GC回收后小于百分之多少抛出OutO fMemoryError
-Xloggc:/home/logs/gc.log #GC日志路径，重启后会被清空
-XX:+PrintCommandLineFlags #将每次JVM启动的参数输出到stdout，以供追溯。
-XX:-OmitStackTraceInFastThrow #对一些特定的异常类型（NullPointerException、ArithmeticException、ArrayIndexOutOfBoundsException、ArrayStoreException、ClassCastException）的Fast Throw优化，如果检测到在代码里某个位置连续多次抛出同一类型异常的话，会用Fast Throw方式来抛出异常，不带上异常栈信息。在连续抛出大量重复异常并且很难回溯前面完整栈信息时可以关闭此选项使得不会进行Fast Throw优化
#-XX:+UseGCLogFileRotation #开启GC日志滚动输出
#-XX:NumberOfGCLogFiles=100 #轮转日志数目最大为100,超过则覆盖
#-XX:GCLogFileSize=100M #GC轮转日志最大尺寸100mb，超过则另起一个日志文件
-XX:+HeapDumpOnOutOfMemoryError #在OOM发生时。JVM将要crash之前，输出Heap Dump
#-XX:+HeapDumpBeforeFullGC #Full GC前进行一次堆转储
#-XX:+HeapDumpAfterFullGC #Full GC后进行一次堆转储
-XX:HeapDumpPath=[path] #堆转储文件的保存位置
-XX:ErrorFile=/home/logs/hs_err_%p.log #JVM crash时，HotSpot会生成一个error文件，提供JVM状态信息的细节
```

