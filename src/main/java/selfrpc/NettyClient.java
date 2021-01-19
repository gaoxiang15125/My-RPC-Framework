package selfrpc;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import selfrpc.sub.NettyClientHandler;
import selfrpc.util.NettyKryoDecoder;
import selfrpc.util.NettyKryoEncoder;
import selfrpc.util.SelfSerializer;
import selfrpc.util.bean.RPCRequest;
import selfrpc.util.bean.RPCResponse;
import selfrpc.util.impl.KryoSerializer;
import selfrpc.util.vaildenum.SystemDefine;

/**
 * @program: MyRpcStudy
 * @description: Netty 数据传输 客户端实现类，用于链接服务器并进行交互
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 17:57
 **/
@Slf4j
public class NettyClient {

    private final String host;
    private final int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化相关资源， eg: EventLoopGroup, Bootstrap;
    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        SelfSerializer kryoSerializer = new KryoSerializer();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 添加超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /**
                         * request编码 -> response解码 -> sub处理返回结果
                         */
                        // RpcResponse -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RPCResponse.class));
                        // ByeBuf -> RpcRequest
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RPCRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 发送消息到 服务端
     * @param rpcRequest 消息体
     * @return 服务端返回的数据
     */
    public RPCResponse sendMessage(RPCRequest rpcRequest) {
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            log.info("客户端连接的地址为 {}", host + ":" + port);
            Channel futureChannel = channelFuture.channel();
            log.info("开始发送消息");
            if(futureChannel != null) {
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                   if(future.isSuccess()) {
                       log.info("客户端发送的信息为: [{}]", JSON.toJSONString(rpcRequest));
                   } else {
                       log.info("客户端发送信息失败");
                   }
                });
                // 阻塞等待，直到 Channel 关闭
                futureChannel.closeFuture().sync();
                // 将服务端返回的数据作为结果返回
                return futureChannel.attr(SystemDefine.CLIENT_ATTRIBUTE_KEY).get();
            }
        } catch (InterruptedException e) {
            log.error("链接服务器过程中出现错误", e);
        }
        return null;
    }
}

/**
 * <pre>
 *                                                 I/O Request
 *                                            via {@link io.netty.channel.Channel} or
 *                                        {@link io.netty.channel.ChannelHandlerContext}
 *                                                      |
 *  +---------------------------------------------------+---------------+
 *  |                           ChannelPipeline         |               |
 *  |                                                  \|/              |
 *  |    +---------------------+            +-----------+----------+    |
 *  |    | Inbound Handler  N  |            | Outbound Handler  1  |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  |               |
 *  |               |                                  \|/              |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |    | Inbound Handler N-1 |            | Outbound Handler  2  |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  .               |
 *  |               .                                   .               |
 *  | ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
 *  |        [ method call]                       [method call]         |
 *  |               .                                   .               |
 *  |               .                                  \|/              |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |    | Inbound Handler  2  |            | Outbound Handler M-1 |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  |               |
 *  |               |                                  \|/              |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |    | Inbound Handler  1  |            | Outbound Handler  M  |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  |               |
 *  +---------------+-----------------------------------+---------------+
 *                  |                                  \|/
 *  +---------------+-----------------------------------+---------------+
 *  |               |                                   |               |
 *  |       [ Socket.read() ]                    [ Socket.write() ]     |
 *  |                                                                   |
 *  |  Netty Internal I/O Threads (Transport Implementation)            |
 *  +-------------------------------------------------------------------+
 * </pre>
 */