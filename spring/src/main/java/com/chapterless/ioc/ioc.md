# Spring IOC 容器
Spring最基本的功能是作为Bean的管理工具，包括被管理Bean的信息注册，Bean的生命周期管理，Bean的作用域管理
## 1.Bean信息注册
### 1.1 BeanDefinition
#### 1.1.1 Bean Role
#### 1.1.2 Bean Scope
#### 1.1.3 Bean LazyInit
#### 1.1.4 Bean DependsOn
#### 1.1.5 Bean Primary
#### 1.1.6 Bean initMethodName
#### 1.1.7 Bean destroyMethodName
#### 1.1.8 Bean name

### 1.2 BeanDefinitionRegistry BeanDefinition解析出来后的存储容器
### 1.2.1 注册BeanDefinition
### 1.2.2 移除BeanDefinition
### 1.2.3 根据beanName获得BeanDefinition
### 1.2.4 根据beanName判断是否已经注册
### 1.2.5 获取所有注册的beanName
### 1.2.6 获得已注册BeanDefinition数量

### 1.3 BeanDefinitionRegistryPostProcessor
### 1.3.1 提供对BeanDefinitionRegistry进行修改的扩展点

### 1.4 来自固定途径的BeanDefinition注册
#### 1.4.1 引入途径
##### 1.4.1.1 SpringBoot程序启动引导类解析引入
##### 1.4.1.2 SpringBoot EnableAutoConfiguration机制解析引入
#### 1.4.2 解析处理点
##### 1.4.2.1 类上的@Import注解
###### 1.4.2.1.1 @Import注解中的ImportSelector接口及其子接口（DeferredImportSelector）实现类
###### 1.4.2.1.2 @Import注解中的ImportBeanDefinitionRegistrar接口实现类
##### 1.4.2.2 类中带@Bean注解的方法
##### 1.4.2.3 类上@EnableConfigurationProperties引入的配置类
### 1.5 通过扫描应用包获得的BeanDefinition注册
### 1.6 Spring代码中直接注册BeanDefinition
### 1.7 条件化判断是否进行BeanDefinition注册
### 1.8 AutoConfiguration解析顺序控制逻辑
### 1.9 @Configurable类处理（不托管给Spring，但是能进行依赖注入）

## 2.Bean的生命周期

### 2.1 处理Bean生命周期的Bean
### 2.1.1 处理

## 3.Bean的作用域管理
