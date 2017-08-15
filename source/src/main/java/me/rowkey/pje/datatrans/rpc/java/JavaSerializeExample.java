package me.rowkey.pje.datatrans.rpc.java;

import me.rowkey.pje.common.meta.User;

import java.io.*;

/**
 * Created by Bryant.Hang on 2017/8/4.
 */
public class JavaSerializeExample {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        User user = new User();

        //序列化
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(user);
        byte[] bytes = bout.toByteArray();

        //反序列化
        ObjectInputStream bin = new ObjectInputStream(new ByteArrayInputStream(bytes));
        bin.readObject();
    }
}
