package selfrpc.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import main.ClientMain;
import selfrpc.client.sub.NettyServerHandler;
import selfrpc.util.NettyKryoDecoder;
import selfrpc.util.NettyKryoEncoder;
import selfrpc.util.SelfSerializer;
import selfrpc.util.bean.RPCRequest;
import selfrpc.util.bean.RPCResponse;
import selfrpc.util.impl.KryoSerializer;

/**
 * @program: MyRpcStudy
 * @description: 使用 Netty 进行数据传输的服务端，主要做监听端口、发送、接受数据的操作
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 15:41
 **/
@Slf4j
public class NettyServer {
    private final int port;

    // 目前暂不添加线程池等，使用最基本的启动方式
    public static void startServer() {
        NettyServer nettyServer = new NettyServer(ClientMain.port);
        nettyServer.run();
    }

    private NettyServer(int port) {
        this.port = port;
    }

    private void run() {
        // 声明一次，以后都通用啦
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        SelfSerializer kryoSerializer = new KryoSerializer();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // 声明传输基于 NIO
                    .channel(NioServerSocketChannel.class)
                    // 开启 Nagle 算法：尽可能的发送大数据块，减少网络传输次数
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 开启 TCP 心跳监测机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 可以存在已经完成三次握手的请求的队列的最大长度
                    // 也就是说一个端口可以建立任意数量的链接，额 尴尬 这不是当然嘛
                    .childOption(ChannelOption.SO_BACKLOG, 128)
                    // 声明日志 记录路径
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 声明 服务器接受信息后，处理流程： 解密-生成结果-加密-返回结果
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 管道、生产线
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RPCRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RPCResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            // 绑定端口，用来与客户端建立链接
            ChannelFuture channelFuture = b.bind(port).sync();
            // 等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务器创建过程中出现错误: ", e);
        } finally {
            // 为什么创建完成后关闭两个 LoopGroup ？
          bossGroup.shutdownGracefully(); // 优雅的
          workerGroup.shutdownGracefully();
        }
    }
}
