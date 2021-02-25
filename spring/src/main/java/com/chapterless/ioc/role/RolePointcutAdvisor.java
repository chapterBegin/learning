package com.chapterless.ioc.role;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;


@Component
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RolePointcutAdvisor implements PointcutAdvisor {

    @Override
    public Pointcut getPointcut() {
        return null;
    }

    @Override
    public Advice getAdvice() {
        return null;
    }

    @Override
    public boolean isPerInstance() {
        return false;
    }
}
