package thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class ServerDemo {
    public static void main(String[] args) throws TTransportException {
        int port= 9000 ;
        // *) 传输层(Transport), 设置监听端口为9000
        TServerSocket serverSocket = new TServerSocket(port);
        // *) 协议层
        TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory(true, true);
        // *) 处理层(Processor)
        HelloServiceImpl handler = new HelloServiceImpl();
        HelloService.Processor<HelloServiceImpl> processor = new HelloService.Processor<HelloServiceImpl>(handler);
        // *) 服务层(Server)
        TServer server = new TThreadPoolServer(
                new TThreadPoolServer.Args(serverSocket)
                        .protocolFactory(protocolFactory)
                        .processor(processor));

        // *) 启动监听服务
        server.serve();
    }
}
