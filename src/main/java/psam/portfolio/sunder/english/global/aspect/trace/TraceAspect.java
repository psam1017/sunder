package psam.portfolio.sunder.english.global.aspect.trace;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class TraceAspect {

    @Around("@annotation(trace)")
    public Object doTrace(ProceedingJoinPoint pjp, Trace trace) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = pjp.proceed();
        long endTime = System.currentTimeMillis();

        if (trace.signature()) {
            log.info("[Trace - Signature] {} : args = {}", pjp.getSignature(), pjp.getArgs());
        }

        if (trace.runtime()) {
            log.info("[Trace - Runtime] {} : {}ms", pjp.getSignature().getName(), endTime - startTime);
        }

        return proceed;
    }
}
