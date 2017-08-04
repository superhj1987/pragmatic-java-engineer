package me.rowkey.datatrans.rpc.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Bryant.Hang on 2017/8/4.
 */
public class ThriftTest {
    public static void main(String[] args){
        TestUser user = new TestUser();
        user.setMobile("xxx");
        user.setName("xxx");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            user.write(new TBinaryProtocol(new TIOStreamTransport(bos)));
        } catch (TException e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] result = bos.toByteArray();

        ByteArrayInputStream bis = new ByteArrayInputStream(result);
        user = new TestUser();
        try {
            user.read(new TBinaryProtocol(new TIOStreamTransport(bis)));
        } catch (TException e) {
            e.printStackTrace();
        }finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void testRpcServer() throws TTransportException {
        TProcessor tprocessor = new TestService.Processor<TestService.Iface>(new TestServiceImpl());

        TServerSocket serverTransport = new TServerSocket(8088);
        TServer.Args tArgs = new TServer.Args(serverTransport);
        tArgs.processor(tprocessor);
        tArgs.protocolFactory(new TBinaryProtocol.Factory());

        // 简单的单线程服务模型
        TServer server = new TSimpleServer(tArgs);
        server.serve();
    }

    public void testRpcClient() {
        TTransport transport = new TSocket("localhost", 8088, 3000);

        TestService.Client testService =
                new TestService.Client(new TBinaryProtocol(transport));
        try {
            transport.open();
            int result = testService.add(1, 2);
            System.out.println(result);
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            transport.close();
        }
    }
}
