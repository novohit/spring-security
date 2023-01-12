package com.example.speldemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@SpringBootTest
class SpelDemoApplicationTests {

    // 构造解析器
    SpelExpressionParser parser = new SpelExpressionParser();

    @Autowired
    BeanFactory beanFactory;

    /**
     * 解析Bean
     */
    @Test
    void test06() {
        String exp = "@bd.hello()";

        Expression expression = parser.parseExpression(exp);
        // 创建上下文环境并设置值
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 设置bean解析器
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        // 传入上下文环境
        String value = expression.getValue(context, String.class);
        System.out.println("value = " + value);
    }

    /**
     * 解析方法表达式
     */
    @Test
    void test05() {
        // 解析无参方法
        String exp1 = "hello()";
        // 解析有参方法
        String exp2 = "hello('novo')";
        Expression expression = parser.parseExpression(exp1);
        // 创建上下文环境并设置值
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(new UserService());
        // 传入上下文环境
        String value = expression.getValue(context, String.class);
        System.out.println("value = " + value);
    }

    /**
     * 解析对象表达式
     */
    @Test
    void test04() {
        String exp = "#user";
        Expression expression = parser.parseExpression(exp);
        User user = new User(1L, "novo");
        // 创建上下文环境并设置值
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("user", user);
        // 传入上下文环境
        User value = expression.getValue(context, User.class);
        System.out.println("value = " + value);
    }

    /**
     * 将对象设置为根对象
     * 解析对象的属性表达式
     */
    @Test
    void test03() {
        String exp = "name";
        Expression expression = parser.parseExpression(exp);
        User user = new User(1L, "novo");
        // 创建上下文环境并设置值
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 设置为根对象
        context.setRootObject(user);
        // 传入上下文环境
        String value = expression.getValue(context, String.class);
        System.out.println("value = " + value);
    }

    /**
     * 解析对象的属性表达式
     */
    @Test
    void test02() {
        String exp = "#user.name";
        Expression expression = parser.parseExpression(exp);
        User user = new User(1L, "novo");
        // 创建上下文环境并设置值
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("user", user);
        // 传入上下文环境
        String value = expression.getValue(context, String.class);
        System.out.println("value = " + value);
    }

    /**
     * 解析普通的表达式
     */
    @Test
    void test01() {
        String exp1 = "1+2";
        Expression expression = parser.parseExpression(exp1);
        Object value = expression.getValue();
        System.out.println("value = " + value);
    }

    @Test
    void contextLoads() {
    }

}
