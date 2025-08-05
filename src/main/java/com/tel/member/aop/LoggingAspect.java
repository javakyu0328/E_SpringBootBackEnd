package com.tel.member.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 로깅을 위한 AOP Aspect 클래스
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Service 패키지의 모든 메서드에 대한 포인트컷 정의
     */
    @Pointcut("execution(* com.tel.member.service..*(..))") //AOP를 어디에 적용할지 지정
    public void serviceLayer() {}

    /**
     * Controller 패키지의 모든 메서드에 대한 포인트컷 정의
     */
    @Pointcut("execution(* com.tel.member.controller..*(..))")
    public void controllerLayer() {}

    /**
     * 메서드 실행 전 로깅
     */
    @Before("serviceLayer() || controllerLayer()") //"타겟 메서드 실행 전에" 공통 기능을 실행하도록 지정
    public void logBefore(JoinPoint joinPoint) { // 스프링 AOP에서 어떤 메서드가 실행되었는지 간략한 정보를 얻을 때 사용
        log.info("=== 메서드 실행 시작: {} ===", joinPoint.getSignature().toShortString());// joinPoint:현재 AOP가 작동 중인 지점(point) 정보를 담고 있는 객체 ,   getSignature() :현재 실행 중인 메서드의 시그니처(Signature) 정보를 가져옴, toShortString() : 그 시그니처를 간략하게 문자열로 반환 (예: MemberController.login(..))
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            log.info("파라미터: {}", args);
        }
    }

    /**
     * 메서드 실행 후 로깅 (정상 반환)
     */
    @AfterReturning(pointcut = "serviceLayer() || controllerLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("=== 메서드 실행 완료: {} ===", joinPoint.getSignature().toShortString());
        if (result != null) {
            log.info("반환값: {}", result);
        }
    }

    /**
     * 메서드 실행 중 예외 발생 시 로깅
     */
    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("=== 메서드 실행 중 예외 발생: {} ===", joinPoint.getSignature().toShortString());
        log.error("예외 내용: {}", exception.getMessage(), exception);
    }

    /**
     * 메서드 실행 시간 측정
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("메서드 실행 시간: {}ms - {}", 
                    (endTime - startTime), joinPoint.getSignature().toShortString());
            return result;
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            log.error("메서드 실행 시간: {}ms (예외 발생) - {}", 
                    (endTime - startTime), joinPoint.getSignature().toShortString());
            throw throwable;
        }
    }
}