# 你需要的知识储备
* 需要了解什么是二进制，十进制，十六进制。
* 需要了解java的基础数据结构，基本数据类型占用的字节大小。(如：short占用2个字节)
* 需要会基本的linux操作，会安装redis，rabbitmq，mongodb，mysql。
* 基础的java环境搭建，JDK8+、Maven、git、IDEA等环境。
* 项目无vip版本，仅个人爱好开发，实现登录、鉴权、心跳、位置解析，扩展简单。
* 此架构接入过1W+的设备，以前单机最大4G内存压测过5W+的TCP连接，无接入压力。
* 有偿接协议解析，也欢迎大家补充协议我会合并到主干分支上。
* 作者大部分协议都能解析，HJ212-2017、博实结、铁塔换电柜、电池私有BMS协议等。（只要有文档都能搞定）
* 另外不要想着几千块能搞定一整个平台，不可能的。
* 如果此项目对你有帮助，请给个star鼓励下作者吧，谢谢。


# jzx-jt808项目介绍
JT808全称JT/T808协议，协议共有三个版本(JT/T808-2011,JT/T808-2013,JT/T808-2019)

# 模块介绍
```
.
|——jzx-jt808-command        # 服务端指令模块(配置下发等)
|——jzx-jt808-common         # 公共模块(常量、枚举、协议类、工具类等)
|——jzx-jt808-dispatch       # 负载均衡模块(http负载、tcp负载、设备日志查询等)
|——jzx-jt808-hardware       # 设备接入模块(启动即可接入硬件)
|——jzx-jt808-process        # 设备数据消费模块(处理硬件数据)
|——jzx-jt808-service        # 基础服务模块(包含数据库、mongo等)
   |--jzx-jt808-service-db       # 数据库操作公共项目(自己集成mybatis、plus、jpa等)
   |--jzx-jt808-service-mongo    # mongodb操作公共项目
|——jzx-jt808-web            # web服务模块(包含指令下发、自定义web服务[自己实现])
```

# 中间件安装
```shell
redis     安装参考:https://blog.csdn.net/hncdyj/article/details/136527560
rabbitmq  安装参考:https://blog.csdn.net/hncdyj/article/details/136528495
mongodb   安装参考:https://blog.csdn.net/hncdyj/article/details/136529032
mysql     安装参考:https://blog.csdn.net/hncdyj/article/details/136529343
```

# 依赖中间件
```shell
redis     version >= 中间件安装版本即可
rabbitmq  version >= 中间件安装版本即可
mongodb   version >= 中间件安装版本即可
mysql     version >= 中间件安装版本即可
maven     version >= 中间件安装版本即可
```

# QuickStart
### 1.拉取代码
```shell
git clone https://gitee.com/hncdyj/jzx-jt808.git
or
git clone https://github.com/hncdyj123/jzx-jt808.git
```

### 2.项目打包
#### 2.1 在在项目根路径下执行
```shell
mvn clean package
or
mvn clean package -U
```

#### 2.2 进入到jzx-jt808-hardware/target文件夹下
会看到jzx-jt808-hardware-assembly.zip压缩包

#### 2.3 进入到jzx-jt808-process/target文件夹下
会看到jzx-jt808-process-assembly.zip压缩包

#### 2.4 进入到jzx-jt808-dispatch/target文件夹下
会看到jzx-jt808-dispatch-assembly.zip压缩包

#### 2.5 进入到jzx-jt808-web/target文件夹下
会看到jzx-jt808-web-assembly.zip压缩包

### 3.启动项目(linux为样例)
#### 3.1 接入服务(jzx-jt808-hardware)
上传项目包后解压jzx-jt808-hardware-assembly.zip压缩包<br/>
```shell
tar -zxvf jzx-jt808-hardware-assembly.zip
cd jzx-jt808-hardware
```
会看下如下路径：
```shell
.
|——conf                     # 配置文件目录
|——lib                      # 依赖jar包
|——jzx-jt808-hardware.jar   # 项目启动jar包
|——springboot-start.sh      # linux|unix|macos启动脚本
|——springboot-start.cmd     # window启动脚本
```
修改配置文件
```yaml
# 自定义业务配置相关
business:
  netty:
    # netty启动监听端口
    port: 3096
  # 集群配置,单点请置空
  # 集群配置说明xx1(接入点1外网地址) yy1(接入点1别名) zz1(接入点域名或者ip)
  # 以下配置中ip为虚构，如有雷同纯属巧合
  # 示例：domainMap: "{'hd01':'52.12.45.85|hd01.example.com','hd02':'52.12.45.84|hd02.example.com'}"
  domainMap: "{'xx1':'yy1|zz1','xx2':'yy2|zz2'}"

spring:
  datasource:
    # 数据库用户名 需要修改
    username: "xx"
    # 数据库密码 需要修改
    password: "xx"
    # 数据库驱动类 需要修改
    driver-class-name: "org.mariadb.jdbc.Driver"
    # 数据库连接URI 需要修改
    url: "jdbc:mysql://ip:port/db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8"
    type: "com.alibaba.druid.pool.DruidDataSource"
    initial-size: 5
    max-active: 20
    min-idle: 5
    max-wait: 60000
    min-evictable-idle-time-millis: 300000
    max-evictable-idle-time-millis: 300000
    time-between-eviction-runs-millis: 60000
    validation-query: select 1
    validation-query-timeout: -1
    test-on-borrow: false
    test-on-return: false
    test-while-idle: true
    pool-prepared-statements: true
    filters: stat,wall,log4j
    share-prepared-statements: true
  # 主redis配置相关(用于本地session缓存、指令下发等)
  redis:
    primary:
      # 连接地址 需要修改
      host: "xx"
      # 连接端口
      port: 6379
      # 连接密码(生产环境必须开启,防止被攻击) 需要修改
      # password: ""
      # 连接超时时间
      timeout: 2000
      # 使用数据库
      database: 2
      # 连接池最大连接数（使用负值表示没有限制）
      pool-max-active: 10
      # 连接池中的最大空闲连接
      pool-max-idle: 5
      # 连接池最大阻塞等待时间（使用负值表示没有限制）
      pool-max-wait: 2000
      # 连接池中的最小空闲连接
      pool-min-idle: 5
    # 从redis配置相关
    secondary:
      # 连接地址 需要修改
      host: "xx"
      # 连接端口
      port: 6379
      # 连接密码(生产环境必须开启,防止被攻击) 需要修改
      # password: ""
      # 连接超时时间
      timeout: 2000
      # 使用数据库
      database: 2
      # 连接池最大连接数（使用负值表示没有限制）
      pool-max-active: 10
      # 连接池中的最大空闲连接
      pool-max-idle: 5
      # 连接池最大阻塞等待时间（使用负值表示没有限制）
      pool-max-wait: 2000
      # 连接池中的最小空闲连接
      pool-min-idle: 5

  # rabbitmq config
  rabbitmq:
    # 业务rabbitmq配置
    business:
      # ip地址 需要修改
      host: "xx"
      # 用户名 需要修改
      username: "xx"
      # 密码 需要修改
      password: "xx"
      port: 5672
      virtual-host: /
    # 日志收集rabbitmq配置
    logs:
      # ip地址 需要修改
      host: "xx"
      # 用户名 需要修改
      username: "xx"
      # 密码 需要修改
      password: "xx"
      port: 5672
      virtual-host: /
      
  # mongodb多数据源配置
  mongodb:
    # 主mongo配置
    primary:
      # mongo连接URI 需要修改
      uri: "mongodb://ip:port/Business"
      # 
      database: "dbname"
    secondary:
      # mongo连接URI 需要修改
      uri: "mongodb://ip:port/Logs"
      database: "dbname"
```

启动项目
```shell
# 转换脚本 && 添加脚本权限
dos2unix *.sh && chmod +x springboot-start.sh
# 启动项目并指定配置环境(根据conf目录下的xml后缀)
### 启动默认环境
./springboot-start.sh start default
### 启动开发环境
./springboot-start.sh start dev
### 启动测试环境
./springboot-start.sh start test
### 启动测试环境
./springboot-start.sh start prd
```

#### 3.2 数据消费服务(jzx-jt808-process)
上传项目包后解压jzx-jt808-process-assembly.zip压缩包<br/>
```shell
tar -zxvf jzx-jt808-process-assembly.zip
cd jzx-jt808-process
```
会看下如下路径：
```shell
.
|——conf                     # 配置文件目录
|——lib                      # 依赖jar包
|——jzx-jt808-process.jar   # 项目启动jar包
|——springboot-start.sh      # linux|unix|macos启动脚本
|——springboot-start.cmd     # window启动脚本
```
修改配置文件
```yaml
spring:
  datasource:
    # 数据库用户名 需要修改
    username: "xx"
    # 数据库密码 需要修改
    password: "xx"
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 数据库地址 需要修改
    url: "jdbc:mysql://ip:port/db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8"
    type: com.alibaba.druid.pool.DruidDataSource
    initial-size: 5
    max-active: 20
    min-idle: 5
    max-wait: 60000
    min-evictable-idle-time-millis: 300000
    max-evictable-idle-time-millis: 300000
    time-between-eviction-runs-millis: 60000
    validation-query: select 1
    validation-query-timeout: -1
    test-on-borrow: false
    test-on-return: false
    test-while-idle: true
    pool-prepared-statements: true
    filters: stat,wall,log4j
    share-prepared-statements: true

  # rabbitmq config
  rabbitmq:
    business:
      # ip地址 需要修改
      host: "xx"
      # 密码 需要修改
      password: "xx"
      port: 5672
      username: root
      virtual-host: /
    logs:
      # ip地址 需要修改
      host: "xx"
      # 密码 需要修改
      password: "xx"
      port: 5672
      username: root
      virtual-host: /

  # mongodb多数据源配置
  mongodb:
    # 主mongo配置
    primary:
      # mongo连接URI 需要修改
      uri: "mongodb://ip:port/Business"
      # 
      database: "dbname"
    secondary:
      # mongo连接URI 需要修改
      uri: "mongodb://ip:port/Logs"
      database: "dbname"
```

启动项目
```shell
# 转换脚本 && 添加脚本权限
dos2unix *.sh && chmod +x springboot-start.sh
# 启动项目并指定配置环境(根据conf目录下的xml后缀)
### 启动默认环境
./springboot-start.sh start default
### 启动开发环境
./springboot-start.sh start dev
### 启动测试环境
./springboot-start.sh start test
### 启动测试环境
./springboot-start.sh start prd
```

#### 3.3 指令下发服务(jzx-jt808-web)
上传项目包后解压jzx-jt808-web-assembly.zip压缩包<br/>
```shell
tar -zxvf jzx-jt808-web-assembly.zip
cd jzx-jt808-web
```
会看下如下路径：
```shell
.
|——conf                     # 配置文件目录
|——lib                      # 依赖jar包
|——jzx-jt808-web.jar   # 项目启动jar包
|——springboot-start.sh      # linux|unix|macos启动脚本
|——springboot-start.cmd     # window启动脚本
```
修改配置文件
```yaml
spring:
  datasource:
    # 数据库用户名 需要修改
    username: "xx"
    # 数据库密码 需要修改
    password: "xx"
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 数据库地址 需要修改
    url: "jdbc:mysql://ip:port/db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8"
    type: com.alibaba.druid.pool.DruidDataSource
    initial-size: 5
    max-active: 20
    min-idle: 5
    max-wait: 60000
    min-evictable-idle-time-millis: 300000
    max-evictable-idle-time-millis: 300000
    time-between-eviction-runs-millis: 60000
    validation-query: select 1
    validation-query-timeout: -1
    test-on-borrow: false
    test-on-return: false
    test-while-idle: true
    pool-prepared-statements: true
    filters: stat,wall,log4j
    share-prepared-statements: true
    
  # 主redis配置相关(用于本地session缓存、指令下发等)
  redis:
    primary:
      # 连接地址 需要修改
      host: "xx"
      # 连接端口
      port: 6379
      # 连接密码(生产环境必须开启,防止被攻击) 需要修改
      # password: ""
      # 连接超时时间
      timeout: 2000
      # 使用数据库
      database: 2
      # 连接池最大连接数（使用负值表示没有限制）
      pool-max-active: 10
      # 连接池中的最大空闲连接
      pool-max-idle: 5
      # 连接池最大阻塞等待时间（使用负值表示没有限制）
      pool-max-wait: 2000
      # 连接池中的最小空闲连接
      pool-min-idle: 5
    # 从redis配置相关
    secondary:
      # 连接地址 需要修改
      host: "xx"
      # 连接端口
      port: 6379
      # 连接密码(生产环境必须开启,防止被攻击) 需要修改
      # password: ""
      # 连接超时时间
      timeout: 2000
      # 使用数据库
      database: 2
      # 连接池最大连接数（使用负值表示没有限制）
      pool-max-active: 10
      # 连接池中的最大空闲连接
      pool-max-idle: 5
      # 连接池最大阻塞等待时间（使用负值表示没有限制）
      pool-max-wait: 2000
      # 连接池中的最小空闲连接
      pool-min-idle: 5

  # rabbitmq config
  rabbitmq:
    business:
      # ip地址 需要修改
      host: "xx"
      # 密码 需要修改
      password: "xx"
      port: 5672
      username: root
      virtual-host: /
    logs:
      # ip地址 需要修改
      host: "xx"
      # 密码 需要修改
      password: "xx"
      port: 5672
      username: root
      virtual-host: /

  # mongodb多数据源配置
  mongodb:
    # 主mongo配置
    primary:
      # mongo连接URI 需要修改
      uri: "mongodb://ip:port/Business"
      # 
      database: "dbname"
    secondary:
      # mongo连接URI 需要修改
      uri: "mongodb://ip:port/Logs"
      database: "dbname"
```

启动项目
```shell
# 转换脚本 && 添加脚本权限
dos2unix *.sh && chmod +x springboot-start.sh
# 启动项目并指定配置环境(根据conf目录下的xml后缀)
### 启动默认环境
./springboot-start.sh start default
### 启动开发环境
./springboot-start.sh start dev
### 启动测试环境
./springboot-start.sh start test
### 启动测试环境
./springboot-start.sh start prd
```

# 流程参考
请查看doc中的md描述 [演示流程](doc/演示流程.md ':include')

有偿可接各种硬件协议开发