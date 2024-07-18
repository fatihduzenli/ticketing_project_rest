package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {


   // Logger logger = LoggerFactory.getLogger(LoggingAspect.class); we use the @Slf4j annotation instead


    private String getUsername(){  // here we are tying to get info about the user that using this functionality
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SimpleKeycloakAccount userDetails= (SimpleKeycloakAccount) authentication.getDetails(); // this username holds user info in the keycloak
        return userDetails.getKeycloakSecurityContext().getIdToken().getPreferredUsername(); // token has username information that we can get
    }
    @Pointcut("execution(* com.cydeo.controller.ProjectController.*(..)) || execution(* com.cydeo.controller.TaskController.*(..))")
    public void anyProjectAndTaskController(){}

    @Before("anyProjectAndTaskController()")
    public void beforeAnyProjectAndTaskController(JoinPoint joinPoint){

        log.info("Before -> Method: {}, User:{}"
        , joinPoint.getSignature().toShortString()
        ,getUsername());
    }

    @AfterReturning(pointcut = "anyProjectAndTaskController()", returning = "results")
    public void afterReturningAnyProjectAndTaskControllerAdvise (JoinPoint joinPoint, Object results){

        log.info("After Returning -> Method: {}, User:{}, Results: {}"
                , joinPoint.getSignature().toShortString()
                ,getUsername()
                ,results.toString()
                );
    }


    @AfterThrowing(pointcut = "anyProjectAndTaskController()", throwing = "exception")
    public void afterReturningAnyProjectAndTaskControllerAdvise (JoinPoint joinPoint, Exception exception){

        log.info("After Returning -> Method: {}, User:{}, Results: {}"
                , joinPoint.getSignature().toShortString()
                ,getUsername()
                ,exception.getMessage()
        );
    }

}
