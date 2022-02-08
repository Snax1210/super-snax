package org.snax.supersnax.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 操作日志记录处理
 *
 * @author lj
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(* org.snax.supersnax..*.*(..))")
    public void method() {

    }

    @Around(value = "method()")
    public Object logParameterInput(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature;
        assert signature != null;
        if (signature instanceof MethodSignature) {
            methodSignature = (MethodSignature)signature;
            Object target = proceedingJoinPoint.getTarget();
            Method currentMethod =
                target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
            LOGGER.debug(" method is {}, input parameter is {}, output result type is {} ,output result is {}",
                proceedingJoinPoint.getSignature().getDeclaringType() + "#" + currentMethod.getName(),
                Arrays.toString(proceedingJoinPoint.getArgs()),
                currentMethod.getReturnType(),
                result);
        } else {
            LOGGER.debug("this annotation only use for method");
        }
        return result;
    }

}
