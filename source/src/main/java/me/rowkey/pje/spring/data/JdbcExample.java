package me.rowkey.pje.spring.data;

import me.rowkey.pje.common.meta.User;
import org.junit.Assert;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;

/**
 * Created by Bryant.Hang on 2017/8/9.
 */
public class JdbcExample {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "applicationContext-core.xml",
                "applicationContext-mysql.xml");

        UserDao userDao = applicationContext.getBean(UserDao.class);
        userDao.getById(1);
    }
}

@Repository
class UserDao {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public User getById(long id) {
        return jdbcTemplate.queryForObject("select * from test_user where id = ?", new Object[]{id}, User.class);
    }

    public List<User> getByName(String name) {
        return jdbcTemplate.queryForList("select * from test_user where name= ?", new Object[]{name}, User.class);
    }
}

interface IUserService {
    void delUser(long id);
}

@Service
class UserServiceImpl implements IUserService {

    @Resource
    UserDao userDao;

    //处理删除用户业务逻辑，使用@Transactional注解实现该方法的事务管理
    @Transactional
    public void delUser(long id) {
    }
}
