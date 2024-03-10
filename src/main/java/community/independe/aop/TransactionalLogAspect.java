package community.independe.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TransactionalLogAspect {

    @Around("execution(* community.independe..api..*.*(..))")
    public Object logApiExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        String methodName = joinPoint.getSignature().getName();
        log.info("Execution Api time for method " + methodName + ": " + elapsedTime + " milliseconds");

        return result;
    }

    @Around("execution(* community.independe..repository..*.*(..))")
    public Object logRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        String methodName = joinPoint.getSignature().getName();
        log.info("Execution Repository time for method " + methodName + ": " + elapsedTime + " milliseconds");

        return result;
    }
}
