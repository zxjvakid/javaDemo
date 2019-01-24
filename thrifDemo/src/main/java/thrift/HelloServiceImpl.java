package thrift;

import org.apache.thrift.TException;

public class HelloServiceImpl implements HelloService.Iface{
    @Override
    public String hello(String name) throws TException {
        return name;
    }
}
