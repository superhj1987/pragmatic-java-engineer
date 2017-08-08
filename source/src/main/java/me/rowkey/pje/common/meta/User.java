package me.rowkey.pje.common.meta;

import java.io.Serializable;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class User implements Serializable {
    private String name;

    private int age;

    private String gender;

    private String nickName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
