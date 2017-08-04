namespace java me.rowkey.datatrans.rpc.thrift

typedef i32 int
service TestService
{
    int add(1:int n1, 2:int n2),
}