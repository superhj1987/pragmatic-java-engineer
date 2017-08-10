package me.rowkey.pje.spring.common;

import org.junit.Assert;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Properties;

/**
 * Created by Bryant.Hang on 2017/8/9.
 */
public class ELExample {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();
        ctx.refresh();
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(ctx));
        Properties result = parser.parseExpression("@systemProperties").getValue(context, Properties.class);
        Assert.assertEquals(System.getProperties(), result);
    }
}
