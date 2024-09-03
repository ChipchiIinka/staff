package ru.egartech.staff.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogsAspect {
    @Around("execution(* ru.egartech.staff.repository.*.*(..))")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        log.debug("Вызов метода: {} в репозитории: {}", methodName, className);
        log.debug("Переданные аргументы: {}", args);
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Ошибка в методе: {} в репозитории: {}, c аргументами: {}", methodName, className, args);
            log.error("Данные ошибки - сообщение: {}", throwable.getMessage());
            throw throwable;
        }
        long timeTaken = System.currentTimeMillis() - startTime;
        log.debug("Метод {} в репозитории {} выполнен за {} мс", methodName, className, timeTaken);
        return result;
    }
}