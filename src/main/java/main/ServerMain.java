package main;

import selfrpc.service.NettyServer;

/**
 * @program: MyRpcStudy
 * @description: 服务器启动类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 19:18
 **/
public class ServerMain {

    public static void main(String[] args) {
        NettyServer.startServer();
    }
}
