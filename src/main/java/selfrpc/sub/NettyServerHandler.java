package selfrpc.sub;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import selfrpc.util.bean.RPCRequest;
import selfrpc.util.bean.RPCResponse;

import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: MyRpcStudy
 * @description: 服务器端消息处理站  Server-Sub
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 15:14
 **/
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    // 本质利用 Netty 框架实现数据传输、解析

    // 定义信号量？ 需要做什么同步操作嘛
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 当前较为简单，收到消息后直接返回 message
        // 之后添加代理 估计就可以实现方法远程调用了
        try{
            RPCRequest requestInfo = (RPCRequest) msg;
            log.info("服务端 sub 接受信息 [{}], 信号量:[{}]", requestInfo, atomicInteger.getAndIncrement());
            // ? 使用信号量的目的是什么 ？ 目前没有看懂，难道是统计当前调用个数？
            RPCResponse rpcResponse = RPCResponse.builder().resultMessage("等加了代理就告诉你结果").build();
            // netty 网络传输模块
            ChannelFuture channelFuture = ctx.writeAndFlush(rpcResponse);
            // 并没有对信号量进一步操作
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            // 原代码内 此处 无 catch 因为 对于无法转化 的msg 大概回直接抛出
            log.error("---------------------------------------");
            log.error("证明你的猜想了嘛：", e);
            log.error("---------------------------------------");
        } finally {
            // 暂时不明白这样的意义
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理客户端信息过程中出现异常: ", cause);
        ctx.close();
    }
}
