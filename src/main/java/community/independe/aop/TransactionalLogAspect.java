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
    public Object logTransactionExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        String methodName = joinPoint.getSignature().getName();
        log.info("Execution time for method " + methodName + ": " + elapsedTime + " milliseconds");

        return result;
    }
}
