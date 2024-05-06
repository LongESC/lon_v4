package com.lon.aspect;



import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author lon
 * @date 2023/4/1
 */
// 日志处理
@Aspect
@Component
public class LogAspect {

    // 获取日志对象’
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*切点，所有web下的controller类的方法*/
    @Pointcut("execution(* com.lon.controller.*.*(..))")
    public void log(){}

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        logger.info("----------doBefore-----------");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String url = request.getRequestURL().toString();

        String ip = request.getRemoteAddr();
        String Agent = request.getHeader("User-Agent");

        String classMethod = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url, ip, classMethod, args,Agent);
        logger.info("Request : {}",requestLog);

    }

    @After("log()")
    public void doAfter(){
        logger.info("----------doAfter-----------");
    }

    // 返回值
    @AfterReturning(returning = "result",pointcut = "log()")
    public void doAfterReturn(Object result){
//        Result result1 = (Result)result;
//        User user =(User) result1.getData();
//        System.out.println(user.getGender());
//        user.setGender("男");
//        result1.setData(user);
////        if (user.getGender()){
////            user.setName("");
////        }

        logger.info("Result : {}",result);
//        return result1;
    }

    /*记录日志内容*/
    private class RequestLog{
        private String url; // 请求的url
        private String ip;  // 访问者ip
        private String classMethod; // 调用方法
        private Object[] args; // 请求参数
        // 参数

        private String Agent;

        public RequestLog(String url, String ip, String classMethod, Object[] args, String Agent) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
            this.Agent = Agent;
        }

        @Override
        public String toString() {
            return "RequestLog{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) + '\'' +
                    ", Agent='" + Agent +'}';
        }
    }
}



