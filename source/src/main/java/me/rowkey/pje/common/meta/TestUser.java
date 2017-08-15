package me.rowkey.pje.common.meta;

import org.msgpack.annotation.Message;

import java.io.Serializable;

@Message
public class TestUser implements Serializable {

    private String testName;

    private String name;

    private String mobile;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
