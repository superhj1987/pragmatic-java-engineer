package me.rowkey.pje.spring.data;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created by Bryant.Hang on 2017/8/9.
 */
public class RedisExample {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "applicationContext-core.xml",
                "applicationContext-redis.xml");

        StringRedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);

        redisTemplate.opsForValue().set("test_key", "test_value"); //set操作
        redisTemplate.opsForValue().getOperations().delete("test_key"); //del操作
        redisTemplate.opsForHash().put("testKey", "testField", "testValue");  //hset操作
    }
}
