package me.rowkey.pje.advancejava.weapons;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import me.rowkey.pje.common.meta.Address;
import me.rowkey.pje.common.meta.TestUser;
import me.rowkey.pje.common.meta.User;

/**
 * Created by Bryant.Hang on 2017/8/14.
 */
public class OrikaExample {
    public static void main(String[] args) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(User.class, TestUser.class)
                .field("name", "testName")
                .byDefault()
                .register();

        MapperFacade mapper = mapperFactory.getMapperFacade();

        User user = new User();
        user.setName("test");
        Address address = new Address();
        address.setCity("aaa");
        user.setAddress(address);

        TestUser testUser = mapper.map(user, TestUser.class);
        System.out.println(testUser);
    }
}
