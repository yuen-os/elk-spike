package com.elk.spike.elkspike.aspect;

import com.elk.spike.elkspike.exception.HandledServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Aspect
public class LoggerAspect {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ObjectMapper mapper;

    @Description("listen to the whole process of the transaction")
    @Around(value = "within(@com.elk.spike.elkspike.annotation.ProcessListener *)")
    public Object onProcess(ProceedingJoinPoint pjp) throws Throwable {
        return this.loggerBuilder(pjp);
    }

    @Description("listen if theres exception being thrown")
    @AfterThrowing(value = "within(@com.elk.spike.elkspike.annotation.ProcessListener *)", throwing = "ex")
    public void onThrow(JoinPoint joinPoint, Throwable ex) throws Throwable {
        this.errorLogBuilder(joinPoint, ex);
    }

    @Description("build logs for throwing error")
    private void errorLogBuilder(JoinPoint joinPoint, Throwable ex) throws Throwable {

        String currentSource = joinPoint.getTarget().getClass().getSimpleName() + "."
                + joinPoint.getSignature().getName();


        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Map<String, Object> params = this.extractMethodArgs(joinPoint, methodSignature);
        params.put("source", currentSource );
        params.put("stackTrace", ExceptionUtils.getStackTrace(ex).replaceAll("\\s+", " ") );

        if(ex instanceof HandledServiceException){
            logger.debug("{}", mapper.writeValueAsString(params));
        }else{
            logger.error("{}", mapper.writeValueAsString(params));
        }

    }

    private Map<String, Object> extractMethodArgs(JoinPoint pjp, MethodSignature methodSignature)
            throws NoSuchMethodException, JsonProcessingException {

        String methodName = methodSignature.getName();
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        Method method = pjp.getTarget().getClass().getDeclaredMethod(methodName, parameterTypes);
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = pjp.getArgs();

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramAnnotations.length; i++) {

            String paramName = parameterNames[i];
            Object value = args[i];
            params.put(paramName,  Objects.nonNull(value) && value.getClass().getPackage().getName().startsWith("java.lang") ? value : null );
        }
        return params;
    }


    @Description("build log for transaction")
    private Object loggerBuilder(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();

        String currentSource = pjp.getTarget().getClass().getSimpleName() + "."
                + pjp.getSignature().getName();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

        Map processInfoMap = this.extractMethodArgs(pjp, methodSignature);
        processInfoMap.put("source", currentSource);
        long elapsedTime = System.currentTimeMillis() - start;
        processInfoMap.put("processTime", elapsedTime);

        logger.info("{}",  mapper.writeValueAsString(processInfoMap));

        return pjp.proceed();
    }
}
