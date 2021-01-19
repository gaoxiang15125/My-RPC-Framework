package selfrpc.service.sub;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import selfrpc.util.bean.RPCResponse;
import selfrpc.util.vaildenum.SystemDefine;

/**
 * @program: MyRpcStudy
 * @description: RPC 数据传输 client 部分 sub 实现代码
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 17:38
 **/
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    // 仅仅用来将客户端发送的 RpcResponse 返回到 ChannelHandlerContext 中
    // 至于如何发送消息的，我完全不可见
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try{
            // 等于说 把收到的信息 键值对的形式存储到 ctx 对象中
            RPCResponse rpcResponse = (RPCResponse) msg;
            log.info("接收到的服务器返回信息为: [{}]", JSON.toJSONString(rpcResponse));
            // 声明 AttributeKey 对象，存储值，供客户端解析使用
            // 将服务端的返回结果保存到 AttributeMap 中，将其看作 Channel 的共享数据源
            // AttributeMap 的 key 为 AttributeKey, value 为 Attribute
            ctx.channel().attr(SystemDefine.CLIENT_ATTRIBUTE_KEY).set(rpcResponse);
            ctx.channel().close();
        } catch (Exception e) {
            // 原代码内 此处 无 catch 因为 对于无法转化 的msg 大概回直接抛出
            log.error("---------------------------------------");
            log.error("证明你的猜想了嘛：", e);
            log.error("---------------------------------------");
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端 Sub 捕捉到异常信息: ", cause);
        ctx.close();
    }
}
