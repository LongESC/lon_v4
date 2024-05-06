package com.lon.aspect;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: lon_v3
 * @package: com.lon.lon_v3.config
 * @className: TransactionConfig
 * @author: LONZT
 * @description: TODO
 * @date: 2023/5/11 8:58
 * @version: 1.0
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    /**
     * 配置全局事务的切点为service层的所有方法  AOP切面表达式 可参考
     * TODO 设置service层所在位置
     */
    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.lon.lon_v3.service..*.*(..))";
    private static final String[] REQUIRED_RULE_TRANSACTION = {"insert*", "create*", "add*", "save*","modify*", "update*", "del*", "delete*"};
    private static final String[] READ_RULE_TRANSACTION = {"select*", "get*", "query*", "search*", "count*","detail*", "find*"};
    /**
     * 注入事务管理器
     */
    @Autowired
    private TransactionManager transactionManager;
    /**
     * 配置事务拦截器
     */
    @Bean
    public TransactionInterceptor txAdvice() {

        RuleBasedTransactionAttribute txAttrRequired = new RuleBasedTransactionAttribute();
        txAttrRequired.setName("REQUIRED事务");
        System.out.println("REQUIRED事务");
        //设置事务传播机制，默认是PROPAGATION_REQUIRED：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务
        txAttrRequired.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //设置异常回滚为Exception  默认是RuntimeException
        List rollbackRuleAttributes = new ArrayList<>();
        rollbackRuleAttributes.add(new RollbackRuleAttribute(Exception.class));
        txAttrRequired.setRollbackRules(rollbackRuleAttributes);

        RuleBasedTransactionAttribute txAttrRequiredReadOnly = new RuleBasedTransactionAttribute();
        txAttrRequiredReadOnly.setName("SUPPORTS事务");
        //设置事务传播机制，PROPAGATION_SUPPORTS：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行
        txAttrRequiredReadOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        //设置异常回滚为Exception  默认是RuntimeException
        txAttrRequiredReadOnly.setRollbackRules(rollbackRuleAttributes);
        txAttrRequiredReadOnly.setReadOnly(true);
        /*事务管理规则，声明具备事务管理的方法名*/
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        //方法名规则限制，必须以下列开头才会加入事务管理当中
        for (String s : REQUIRED_RULE_TRANSACTION) {
            source.addTransactionalMethod(s, txAttrRequired);
        }
        //对于查询方法，根据实际情况添加事务管理 可能存在查询多个数据时，已查询出来的数据刚好被改变的情况
        for (String s : READ_RULE_TRANSACTION) {
            source.addTransactionalMethod(s, txAttrRequired);
        }
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor((PlatformTransactionManager) transactionManager, source);
        return transactionInterceptor;
    }
    /**
     *  设置切面
     */
    @Bean
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice());
    }
}

