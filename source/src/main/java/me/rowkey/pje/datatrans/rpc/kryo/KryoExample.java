package me.rowkey.pje.datatrans.rpc.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.rowkey.pje.common.meta.TestUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Bryant.Hang on 2017/8/15.
 */
public class KryoExample {
    public static void main(String[] args) {
        Kryo kryo = new Kryo();

        // 序列化
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Output output = new Output(os);
        TestUser user = new TestUser();
        kryo.writeObject(output, user);
        output.close();
        byte[] bytes = os.toByteArray();

        // 反序列化
        Input input = new Input(new ByteArrayInputStream(bytes));
        user = kryo.readObject(input, TestUser.class);
        input.close();

    }
}
