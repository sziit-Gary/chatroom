# java swing实现聊天室

#### 介绍
实现多个客户端之间的群聊、私聊、、私发文件，客户端上传文件等

#### 软件架构
1、JDK14.0（JDK9.0即可）
<br>
2、maven工程
<br>
3、maven依赖：

```
 <dependencies>
        <!-- https://mvnrepository.com/artifact/com.formdev/flatlaf -->
        <dependency>
            <groupId>com.formdev</groupId>
            <artifactId>flatlaf</artifactId>
            <version>0.26</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.10</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.71</version>
        </dependency>

    </dependencies>
```



#### 使用说明

1. 启动starter目录下的Server.java

2. 启动starter目录下的Client.java（可以启动多个）

3. 客户端出现登录界面

   已注册账户：

   1. 账号zpc  密码 zpc
   2. 账户fd    密码fd
   3. 你也可以自己注册然后登陆（redis数据库）

4. 登录成功后进入通信面板

#### 工程结构
![输入图片说明](https://images.gitee.com/uploads/images/2020/0629/091616_69cad842_5657116.png "图片1.png")
##### Javabean目录：存储java实体类。

1. Info：通信的消息报文类，包括消息报文的类型、内容、发送者、接收者、附件。
2. User：客户端（用户）实体，字段为username，password，image，分别是用户名，密码，头像。

##### Service目录：主要的业务流程、界面，包括登录、注册等业务、广播聊天和私聊业务。

1. Starter：对外提供的启动类
2. Client：启动一个客户端
3. Server：启动服务器

##### Util：封装好基本操作的工具类

1. RedisUtils：完成和redis远程服务器的操作，主要存储用户名、密码、通信缓存等
2. SwingUtils：注册组件时候需要的工具类，封装了比如图标比例自适应、等比例设置面板大小等常用功能
3. ToolUtils：包括一些通用功能，比如获取系统时间、获得全局唯一日志对象，获得文件后缀名、读写文件、获得用户头像等。






#### 测试分析
##### 客户端登录界面
![输入图片说明](https://images.gitee.com/uploads/images/2020/0629/091828_2d4e0cf5_5657116.png "图片2.png")

##### 通信面板界面
![输入图片说明](https://images.gitee.com/uploads/images/2020/0629/091919_78d16916_5657116.png "图片3.png")

##### 客户端群聊界面
![输入图片说明](https://images.gitee.com/uploads/images/2020/0629/091946_cbfebd1a_5657116.png "图片5.png")

##### 客户端私聊界面
![输入图片说明](https://images.gitee.com/uploads/images/2020/0629/092008_f0494843_5657116.png "图片12.png")

##### 服务器界面：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0629/092025_e6bad1b5_5657116.png "图片6.png")