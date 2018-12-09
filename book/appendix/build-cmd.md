# 附录A: 代码构建常用命令

## Maven常用命令

Maven版本：3.3.9

- 清除

    `mvn -N clean`
    
    -N参数表示仅仅构建当前目录的项目，不去构建子模块。
- 打包
    
    `mvn package`
- 发布到本地
    
    `mvn install`
- 发布到线上
    
    `mvn deploy`
- 将依赖复制到指定目录

    `mvn dependency:copy-dependencies -DoutputDirectory=./lib`
    
- 部署非Maven项目的jar包

    `mvn deploy:deploy-file -DgroupId=[groupId] -DartifactId=[artifactId] -Dversion=[version] -Dpackaging=jar -Dfile=[jarFilePath]  -Durl=[repositoryUrl]`
    
- 安装非Maven项目的jar包到本地

    `mvn install:install-file -DgroupId=[groupId] -DartifactId=[artifactId] -Dversion=[version] -Dpackaging=jar -Dfile=[jarFilePath]`

- 执行指定类中的main方法

    `mvn exec:java -Dexec.mainClass=[mainClass]`
    
- 查看依赖树

    `mvn dependency:tree`
    
- 执行指定的测试用例

    `mvn test -Dtest=[ClassName]#[MethodName] ` #[MethodName]为要运行的方法名，支持*通配符
    
- 跳过测试阶段且不编译测试用例类

    `mvn -Dmaven.test.skip=true ...`
    
- 跳过测试阶段但编译测试用例类

    `mvn -DskipTests ...`
    
- 使用指定的pom文件或者指定目录下的pom.xml运行

    `mvn -f [file/dir] ...`
        
- 从已有项目生成archetype

    `mvn archetype:create-from-project`
    
- 从Maven库搜索archetype

    `mvn archetype:crawl`
    
- 根据archetype生成项目

    `mvn archetype:generate -DgroupId=[groupId]  -DartifactId=[artifactId] -DarchetypeArtifactId=[archetypeArtifactId] -DarchetypeVersion=[archetypeVersion]  -DarchetypeCatalog=[archetypeCatalogPath]`
    
    需要注意，3.x版本去掉了archetypeRepository参数并且修改了archetypeCatalog参数，需要在settings.xml中配置repository。如果需要在参数中设置，可以使用mvn org.apache.maven.plugins:maven-archetype-plugin:2.4:generate配合-DarchetypeRepository=http://xxx[远程repository的url]或者-DarchetypeCatalog=http://xxx[catalog的远程url]来使用远程的archetype。
    
此外，可以使用`-q`参数使Maven的日志输出只包含错误信息。

## Gradle常用命令

Gradle版本：2.4

- 执行特定的task

    `gradle [taskName]`
    
- 清除

    `gradle clean`

- 构建

    `gradle build`
    
- 跳过测试构建

    `gradle build -x test`

- 显示task之间的依赖关系
    
    `gradle tasks --all`  
    
- 查看testCompile的依赖情形

    `gradle -q dependencies --configuration testCompile` 

- 继续执行task而忽略前面失败的task

    `gradle build --continue`
    
- 使用指定的gradle文件调用task

    `gradle -b [file_path] [task]`

- 使用指定的目录调用task

    `gradle -q -p [dir] helloWorld`
    
    在指定目录搜索settings.gradle和build.gradle文件。
    
- 产生build运行时间的报告

    `gradle build --profile`
    
    结果存储在build/report/profile目录，名称为build运行的时间。
    
- 试运行build

    `gradle -m build`
    
- Gradle的图形界面

    `gradle --gui`
    
此外，Gradle的命令日志输出有ERROR（错误信息）、QUIET（重要信息）、WARNGING（警告信息）、LIFECYCLE（进程信息）、INFO（一般信息）、DEBUG（调试信息）一共六个级别。在执行Gradle task时可以适时的调整信息输出等级，以便方便地观看执行结果：

- -q/--quiet启用重要信息级别，该级别下只会输出自己在命令行下打印的信息及错误信息。
- -i/--info会输出除debug以外的所有信息。
- -d/--debug会输出所有日志信息。
- -s/--stacktrace会输出详细的错误堆栈。

