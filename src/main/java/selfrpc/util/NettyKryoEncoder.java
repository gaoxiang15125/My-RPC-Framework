package selfrpc.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @program: MyRpcStudy
 * @description: Netty + Kryo 实现的 RPC 编码器
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 15:00
 **/
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    /**
     * 协议描述 第一版 自定义 Rpc 协议
     * | 数据长度描述符 | 数据 |
     * | -------------- | ---- |
     * | 4B             | xxxB |
     */

    private final SelfSerializer selfSerializer;
    private final Class<?> genericClass;

    /**
     * 将对象转化为字节码，写入 ByteBuf 对象中，共 Netty 传输
     * @param ctx
     * @param msg 需要传输的数据
     * @param out 加密后数据传输媒介
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        // 为什么一个编码器对象要对应一个类？ 会不会过于庞杂了
        // 可不可以让他 通用些
        if(genericClass.isInstance(msg)) {
            // 较为简单不再注释
            byte[] body = selfSerializer.serialize(msg);
            out.writeInt(body.length);
            out.writeBytes(body);
        }
    }
}
