# Spring IOC 容器
Spring最基本的功能是作为Bean的管理工具，包括被管理Bean的信息注册，Bean的生命周期管理，Bean的作用域管理
## 1.Bean信息注册
### 1.1 BeanDefinition
#### 1.1.1 Bean Role
+ BeanDefinition.ROLE_APPLICATION 普通Bean角色，非特殊Bean的默认值，标有@Component（及其继承者注解@Controller,@Service,@Repository,@Configuration）
+ BeanDefinition.ROLE_SUPPORT Spring代码中未见使用
+ BeanDefinition.ROLE_INFRASTRUCTURE 基础设施Bean角色，目前已知使用方式:PointcutAdvisor或者IntroductionAdvisor（或者子接口）实现类想要生效时需通过@Role注解指定为该角色，否则默认ROLE_APPLICATION级别将不会被注册为切面，只是一个普通Bean
#### 1.1.2 Bean Scope
##### 1.1.2.1 传统意义上的Scope
+ ConfigurableBeanFactory.SCOPE_SINGLETON 单例，任何时间从BeanFactory获取都是同一个Bean，Bean的引用缓存在DefaultSingletonBeanRegistry中
+ ConfigurableBeanFactory.SCOPE_PROTOTYPE 原型，任何时间从BeanFactory获取都是不同的Bean，Bean的引用不缓存
+ Request 请求，同一个请求内从BeanFactory获取都是同一个Bean,不同请求获得的Bean不同，Bean的引用存储在Request中
+ Session 会话，同一个会话内从BeanFactory获取都是同一个Bean,不同会话获得的Bean不同，Bean的引用存储在Session中
+ Global-Session 在一个全局的HttpSession中，容器会返回该Bean的同一个实例，仅在使用PortletContext时有效，Bean的引用存储在Portlet Context中
> 通过@Scope注解进行设置，上述所有Scope生效的时机都是从BeanFactory获取Bean时生效，已经依赖注入的Bean在使用时并不会发生变化

#### 1.1.2.2 更为不常用的Scope 
+ LazyInit 被调用时才真正生成对应的对象
> LazyInitTargetSource实现，getTarget方法中调用BeanFactory.getBean方法获取Bean
+ Singleton 
> SingletonTargetSource实现，getTarget方法直接返回原来的target
+ Prototype 每次调用都重新拿一个对象
> PrototypeTargetSource实现，getTarget方法中调用BeanFactory.getBean方法获取Bean
+ ThreadLocal 每个线程一个对象
> ThreadLocalTargetSource，getTarget方法先从ThreadLocal获取，如果没有则获取一个Prototype对象
+ HotSwappable 可替换的对象
> HotSwappableTargetSource实现，提供swap方法进行Bean替换
+ Refreshable 可刷新的对象
> AbstractRefreshableTargetSource 可继承抽象类实现具体刷新方式，Spring中提供BeanFactoryRefreshableTargetSource,刷新方式为从BeanFactory重新获取Bean
+ Pooling  从对象池里面拿对象
> AbstractPoolingTargetSource 可继承抽象类实现具体对象池的处理，Spring中提供CommonsPool2对象池实现方式
+ ...
> 无法通过简单的设置进行实现，生效时机为Bean被真正调用的时候，也就是说调用时通过代理先获取真实的Bean，再用获得的Bean进行数据处理

#### 1.1.3 Bean LazyInit
> 对于在类上添加了@Lazy注解的Bean，该Bean的LazyInit属性值为true，Spring容器会延迟该bean的生命周期处理，当该Bean被其他Bean依赖，会在注入时进行该Bean的生命周期处理
#### 1.1.4 Bean DependsOn
> DependsOn表示该Bean依赖相应的Bean,在进行该Bean的生命周期处理之前，会先处理DependsOn中的Bean的生命周期
#### 1.1.5 Bean Primary
> @Primary是用于当某个接口存在多个实现Bean的时候，优先选择标有@Primary的Bean进行依赖注入
#### 1.1.6 Bean initMethodName
> initMethodName是指Bean中标有@PostConstruct注解的方法，在bean生命周期的初始化过程中会被调用
#### 1.1.7 Bean destroyMethodName
> destroyMethodName是指Bean中标有@PreDestory注解的方法，会在程序关闭时被调用

### 1.2 BeanDefinitionRegistry BeanDefinition解析出来后的存储容器
+ 注册BeanDefinition
+ 移除BeanDefinition
+ 根据beanName获得BeanDefinition
+ 根据beanName判断是否已经注册
+ 获取所有注册的beanName
+ 获得已注册BeanDefinition数量

### 1.3 BeanDefinitionRegistryPostProcessor
### 1.3.1 提供对BeanDefinitionRegistry进行操作的扩展点
+ 解析AutoConfiguration类获得BeanDefinition并将其注册到BeanDefinitionRegistry
> ConfigurationClassPostProcessor
+ 解析扫描包获得BeanDefinition并将其注册到BeanDefinitionRegistry
> MyBatis的MapperScannerConfigurer

### 1.4 来自固定途径的BeanDefinition注册
#### 1.4.1 引入途径
##### 1.4.1.1 SpringBoot程序启动引导类解析引入
> 自定义的Main函数所在类，通常标有@SpringBootApplication
##### 1.4.1.2 SpringBoot EnableAutoConfiguration机制解析引入
##### 1.4.1.3 解析XML文件获得的BeanDefinition并将其注册到BeanDefinitionRegistry
#### 1.4.2 解析处理点
##### 1.4.2.1 类上的@Import注解
###### 1.4.2.1.1 @Import注解中的ImportSelector接口及其子接口（DeferredImportSelector）实现类
###### 1.4.2.1.2 @Import注解中的ImportBeanDefinitionRegistrar接口实现类
##### 1.4.2.2 类中带@Bean注解的方法
##### 1.4.2.3 类上@EnableConfigurationProperties引入的配置类

### 1.5 通过扫描应用包获得的BeanDefinition注册
+ Scanner实现BeanDefinitionRegistryPostProcessor接口
> MyBatis的MapperScannerConfigurer
+ Scanner实例化时BeanDefinitionRegistry对象作为构造方法参数传入
> Spring的ClassPathBeanDefinitionScanner

### 1.6 Spring代码中直接注册BeanDefinition
### 1.7 条件化判断是否进行BeanDefinition注册
### 1.8 AutoConfiguration解析顺序控制逻辑
### 1.9 @Configurable类处理（不托管给Spring，但是能进行依赖注入）

## 2.Bean的生命周期

### 2.1 处理Bean生命周期的Bean
### 2.1.1 处理

## 3.Bean的作用域管理
