#原型 
>每次从BeanFactory获取都是不同的Bean，Bean的引用不缓存，每次都进行Bean的创建工作

+ 原型的Bean不能参与循环引用，否则Spring启动将报错，所以BeanFactory中没有原型Bean的缓存。
+ 正常需要使用原型模式，需从controller开始就是原型模式，因为SpringMVC中，由DispatcherServlet进行请求分发时，都会重新从BeanFactory获取对应的ControllerBean，因此才能每次都使用新的Bean。
+ 单例模式的ControllerBean可以依赖注入原型的Service Bean,但是调用的Service Bean都是同一个，因为依赖注入的就是那一个。

> RequestMappingHandlerMapping中handlerMethod的处理
```
@Override
@SuppressWarnings("deprecation")
public void afterPropertiesSet() {

    this.config = new RequestMappingInfo.BuilderConfiguration();
    this.config.setTrailingSlashMatch(useTrailingSlashMatch());
    this.config.setContentNegotiationManager(getContentNegotiationManager());

    if (getPatternParser() != null) {
        this.config.setPatternParser(getPatternParser());
        Assert.isTrue(!this.useSuffixPatternMatch && !this.useRegisteredSuffixPatternMatch,
                "Suffix pattern matching not supported with PathPatternParser.");
    }
    else {
        this.config.setSuffixPatternMatch(useSuffixPatternMatch());
        this.config.setRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch());
        this.config.setPathMatcher(getPathMatcher());
    }
    //此处调用父类AbstractHandlerMethodMapping的afterPropertiesSet方法
    super.afterPropertiesSet();
}
```
> AbstractHandlerMethodMapping的afterPropertiesSet
```
/**
 * Detects handler methods at initialization.
 * @see #initHandlerMethods
 */
@Override
public void afterPropertiesSet() {
    initHandlerMethods();
}

/**
 * Scan beans in the ApplicationContext, detect and register handler methods.
 * @see #getCandidateBeanNames()
 * @see #processCandidateBean
 * @see #handlerMethodsInitialized
 */
protected void initHandlerMethods() {
    for (String beanName : getCandidateBeanNames()) {
        if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
            processCandidateBean(beanName);
        }
    }
    handlerMethodsInitialized(getHandlerMethods());
}

/**
 * Determine the names of candidate beans in the application context.
 * @since 5.1
 * @see #setDetectHandlerMethodsInAncestorContexts
 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors
 */
protected String[] getCandidateBeanNames() {
    return (this.detectHandlerMethodsInAncestorContexts ?
            BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class) :
            obtainApplicationContext().getBeanNamesForType(Object.class));
}

/**
 * Determine the type of the specified candidate bean and call
 * {@link #detectHandlerMethods} if identified as a handler type.
 * <p>This implementation avoids bean creation through checking
 * {@link org.springframework.beans.factory.BeanFactory#getType}
 * and calling {@link #detectHandlerMethods} with the bean name.
 * @param beanName the name of the candidate bean
 * @since 5.1
 * @see #isHandler
 * @see #detectHandlerMethods
 */
protected void processCandidateBean(String beanName) {
    Class<?> beanType = null;
    try {
        beanType = obtainApplicationContext().getType(beanName);
    }
    catch (Throwable ex) {
        // An unresolvable bean type, probably from a lazy bean - let's ignore it.
        if (logger.isTraceEnabled()) {
            logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
        }
    }
    if (beanType != null && isHandler(beanType)) {
        detectHandlerMethods(beanName);
    }
}

/**
 * Look for handler methods in the specified handler bean.
 * @param handler either a bean name or an actual handler instance
 * @see #getMappingForMethod
 */
protected void detectHandlerMethods(Object handler) {
    Class<?> handlerType = (handler instanceof String ?
            obtainApplicationContext().getType((String) handler) : handler.getClass());

    if (handlerType != null) {
        Class<?> userType = ClassUtils.getUserClass(handlerType);
        Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
                (MethodIntrospector.MetadataLookup<T>) method -> {
                    try {
                        return getMappingForMethod(method, userType);
                    }
                    catch (Throwable ex) {
                        throw new IllegalStateException("Invalid mapping on handler class [" +
                                userType.getName() + "]: " + method, ex);
                    }
                });
        if (logger.isTraceEnabled()) {
            logger.trace(formatMappings(userType, methods));
        }
        methods.forEach((method, mapping) -> {
            Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
            registerHandlerMethod(handler, invocableMethod, mapping);
        });
    }
}
```
>可以看到registerHandlerMethod时，传入的handler参数实际为beanName,追踪后续编码可以知道生成的HandlerMethod中的bean实际上就是beanName
```
protected void registerHandlerMethod(Object handler, Method method, T mapping) {
    this.mappingRegistry.register(mapping, handler, method);
}

public void register(T mapping, Object handler, Method method) {
    this.readWriteLock.writeLock().lock();
    try {
        HandlerMethod handlerMethod = createHandlerMethod(handler, method);
        validateMethodMapping(handlerMethod, mapping);

        Set<String> directPaths = AbstractHandlerMethodMapping.this.getDirectPaths(mapping);
        for (String path : directPaths) {
            this.pathLookup.add(path, mapping);
        }

        String name = null;
        if (getNamingStrategy() != null) {
            name = getNamingStrategy().getName(handlerMethod, mapping);
            addMappingName(name, handlerMethod);
        }

        CorsConfiguration config = initCorsConfiguration(handler, method, mapping);
        if (config != null) {
            config.validateAllowCredentials();
            this.corsLookup.put(handlerMethod, config);
        }

        this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directPaths, name));
    }
    finally {
        this.readWriteLock.writeLock().unlock();
    }
}

/**
 * Create the HandlerMethod instance.
 * @param handler either a bean name or an actual handler instance
 * @param method the target method
 * @return the created HandlerMethod
 */
protected HandlerMethod createHandlerMethod(Object handler, Method method) {
    if (handler instanceof String) {
        return new HandlerMethod((String) handler,
                obtainApplicationContext().getAutowireCapableBeanFactory(), method);
    }
    return new HandlerMethod(handler, method);
}
```
> 在请求进来需要从获取对应的处理方法时DispatcherServlet的getHandler方法
```
/**
 * Return the HandlerExecutionChain for this request.
 * <p>Tries all handler mappings in order.
 * @param request current HTTP request
 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
 */
@Nullable
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    if (this.handlerMappings != null) {
        for (HandlerMapping mapping : this.handlerMappings) {
            HandlerExecutionChain handler = mapping.getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
    }
    return null;
}
```
> 当循环到RequestMappingHandlerMapping时，最终会调用到AbstractHandlerMethodMapping的getHandlerInternal方法
```
// Handler method lookup

/**
 * Look up a handler method for the given request.
 */
@Override
protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
    String lookupPath = initLookupPath(request);
    this.mappingRegistry.acquireReadLock();
    try {
        HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
        return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
    }
    finally {
        this.mappingRegistry.releaseReadLock();
    }
}
```
>在根据请求路径查询到最合适的handlerMethod后，handlerMethod.createWithResolvedBean方法会进行Bean的获取
```
public HandlerMethod createWithResolvedBean() {
    Object handler = this.bean;
    if (this.bean instanceof String) {
        Assert.state(this.beanFactory != null, "Cannot resolve bean name without BeanFactory");
        String beanName = (String) this.bean;
        handler = this.beanFactory.getBean(beanName);
    }
    return new HandlerMethod(this, handler);
}
```