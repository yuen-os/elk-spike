package com.elk.spike.elkspike.aspect;

import com.elk.spike.elkspike.exception.HandledServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.StringMapMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Aspect
public class LoggerAspect {

    private static final Logger logger = LogManager.getLogger(LoggerAspect.class);
    private final String ECS_PREFIX = "yyy.";

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
        params.put(ECS_PREFIX.concat("source"), currentSource );
        params.put(ECS_PREFIX.concat("stackTrace"), ExceptionUtils.getStackTrace(ex).replaceAll("\\s+", " ") );

        if(ex instanceof HandledServiceException){
            logger.debug( new ObjectMessage(params));
        }else{
            logger.error( new ObjectMessage(params));
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
            params.put(ECS_PREFIX.concat("methodArgs.").concat(paramName),  Objects.nonNull(value) && value.getClass().getPackage().getName().startsWith("java.lang") ? value : null );
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
        processInfoMap.put(ECS_PREFIX.concat("source"), currentSource);
        long elapsedTime = System.currentTimeMillis() - start;
        processInfoMap.put(ECS_PREFIX.concat("processTime"), elapsedTime);

        logger.info(new ObjectMessage(processInfoMap));
        return pjp.proceed();
    }
}
