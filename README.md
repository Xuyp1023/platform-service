# 平台后台服务工程 -- @Service
> 描述：该工程用于编写平台后台服务类的相关代码

# 1、源码路径命名规范
### com.betterjr.modules.{module name}.dao
### com.betterjr.modules.{module name}.dubbo
### com.betterjr.modules.{module name}.entity
### com.betterjr.modules.{module name}.service

## 如: 客户管理模块
### com.betterjr.modules.customer.dao
### com.betterjr.modules.customer.dubbo
### com.betterjr.modules.customer.entity
### com.betterjr.modules.customer.service

# 2、资源文件路径命名规范
### sqlmap.modules.{module name}

## 如: 客户管理模块
### sqlmap.modules.customer

# 3、platform-dubbo-provider.xml配置
### dubbo:annotation package="com.betterjr.modules.{module name}",多个{module name}用逗号","隔开

## 4、spring-context-platform-dubbo-provider.xml配置

### context:component-scan base-package="com.betterjr.modules.{module name}",多个{module name}用分号";"隔开

# end