#简陋版的AOP

> POM文件中未引入
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
> 或者更准却的说是
```
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```
> 未被引入时，AopAutoConfiguration中条件化注册的AbstractAutoProxyCreator是InfrastructureAdvisorAutoProxyCreator，
> 此时并不支持@Aspect注解及相关注解，此时想要进行AOP编程，需基于SpringAop底层接口进行。
> 声明切面通过Pointcut接口，增强处理使用Advisor接口提供Advice增强处理，分开来实现也行，稍微麻烦一点。
> InfrastructureAdvisorAutoProxyCreator在判断PointcutAdvisor或者IntroductionAdvisor是否有资格成为有效的Advisor时，会对其角色进行判断
```
@Override
protected boolean isEligibleAdvisorBean(String beanName) {
    return (this.beanFactory != null && this.beanFactory.containsBeanDefinition(beanName) &&
        this.beanFactory.getBeanDefinition(beanName).getRole() == BeanDefinition.ROLE_INFRASTRUCTURE);
}
```
要求角色必须为BeanDefinition.ROLE_INFRASTRUCTURE，所以需要对RolePointcutAdvisor设置@Role(BeanDefinition.ROLE_INFRASTRUCTURE)

>引入
```
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```
>之后AopAutoConfiguration中条件化注册的AbstractAutoProxyCreator是AnnotationAwareAspectJAutoProxyCreator
>AnnotationAwareAspectJAutoProxyCreator在判断PointcutAdvisor或者IntroductionAdvisor是否有资格成为有效的Advisor时，会调用BeanFactoryAdvisorRetrievalHelper的isEligibleBean方法
```
/**
	 * Determine whether the aspect bean with the given name is eligible.
	 * <p>The default implementation always returns {@code true}.
	 * @param beanName the name of the aspect bean
	 * @return whether the bean is eligible
	 */
	protected boolean isEligibleBean(String beanName) {
		return true;
	}
```
>直接返回true不进行角色判断