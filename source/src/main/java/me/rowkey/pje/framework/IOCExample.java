package me.rowkey.pje.framework;

/**
 * Created by Bryant.Hang on 2017/8/9.
 */
public class IOCExample {

}
interface IUser{
    void say();
}

class AdminUser implements IUser{
    public void say(){
        System.out.println("I'm admin");
    }
}

class IOCTest{
    private IUser user;

    public void setUser(IUser user){
        this.user = user;
    }

    public IUser getUser(){
        return this.user;
    }

    public void test(){
        this.user.say();
    }

    public static void main(String[] args){
        IOCTest test = new IOCTest();
        test.setUser(new AdminUser());

        test.test();
    }
}