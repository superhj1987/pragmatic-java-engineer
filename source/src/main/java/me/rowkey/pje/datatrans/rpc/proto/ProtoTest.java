package me.rowkey.pje.datatrans.rpc.proto;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by Bryant.Hang on 2017/8/4.
 */
public class ProtoTest {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        TestUserProto.TestUser testUser =
                TestUserProto.TestUser.newBuilder()
                        .setMobile("xxx")
                        .setName("xxx")
                        .build();

        byte[] bytes = testUser.toByteArray();

        TestUserProto.TestUser tas = TestUserProto.TestUser.parseFrom(bytes);
        System.out.println(tas);
    }
}
