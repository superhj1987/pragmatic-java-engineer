package me.rowkey.pje.datatrans.rpc.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import me.rowkey.pje.common.meta.TestUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Bryant.Hang on 2017/8/4.
 */
public class HessianExample {
    public static void main(String[] args) throws IOException {
        //序列化
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(os);
        out.startMessage();
        TestUser user = new TestUser();
        out.writeObject(user);
        out.completeMessage();
        out.flush();
        byte[] bytes = os.toByteArray();
        out.close();
        os.close();

        //反序列化
        ByteArrayInputStream ins = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(ins);
        input.startMessage();
        user = (TestUser) input.readObject();
        input.completeMessage();
        input.close();
        ins.close();
    }
}
