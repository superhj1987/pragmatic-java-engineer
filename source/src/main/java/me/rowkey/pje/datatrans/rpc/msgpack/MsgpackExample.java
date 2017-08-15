package me.rowkey.pje.datatrans.rpc.msgpack;

import me.rowkey.pje.common.meta.TestUser;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * Created by Bryant.Hang on 2017/8/15.
 */
public class MsgpackExample {

    public static void main(String[] args) throws IOException {

        TestUser user = new TestUser();
        MessagePack messagePack = new MessagePack();

        //序列化
        byte[] bs = messagePack.write(user);

        //反序列化
        user = messagePack.read(bs, TestUser.class);
    }
}
