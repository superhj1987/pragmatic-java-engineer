package me.rowkey.pje.datatrans.rpc.thrift;

import org.apache.thrift.TException;

/**
 * Created by Bryant.Hang on 2017/8/4.
 */
public class TestServiceImpl implements TestService.Iface {
    @Override
    public int add(int n1, int n2) throws TException {
        return n1 + n2;
    }
}
