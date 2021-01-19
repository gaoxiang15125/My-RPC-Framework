package main;

import selfrpc.client.NettyClient;
import selfrpc.util.bean.RPCRequest;
import selfrpc.util.bean.RPCResponse;

/**
 * @program: MyRpcStudy
 * @description: 客户端启动类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 19:18
 **/
public class ClientMain {

    static String host = "127.0.0.1";
    public static int port = 15125;

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient(host, port);
        // 每次 sendMessage 方法 都是进行链接
        // 可不可以修改为使用长连接实现 ？ 有趣的假设
        RPCRequest rpcRequest = RPCRequest.builder()
                .interfaceName("学海无涯苦作舟")
                .methodName("书山有路勤为径").build();
        for(int i=0;i<3;i++) {
            RPCResponse rpcResponse = nettyClient.sendMessage(rpcRequest);
            System.out.println(rpcResponse);
        }
    }
}
