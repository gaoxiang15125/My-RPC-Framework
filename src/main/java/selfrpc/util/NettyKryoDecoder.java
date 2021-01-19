package selfrpc.util;

import com.esotericsoftware.kryo.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @program: MyRpcStudy
 * @description: Netty 网络传输解码器
 * 网络传输需要通过字节流来实现，
 * ByteBuf 可以看作是 Netty 提供的字节数据的容器，使用它会让我们更加方便地处理字节数据
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 10:55
 **/
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    // 自定义的 序列化解析方式 可以理解为 自定义 RPC 协议
    private final SelfSerializer serializer;
    private final Class<?> genericClass;

    /**
     * 我们自定义的序列化实现，将字节流长度存储到了头部，Integer 类型，长度为 4
     */
    private static final int BODY_LENGTH = 4;

    /**
     * 4.12 版本取消了返回值，使用 out 参数接收解析后的结果
     * @param ctx 解析器获取对象
     * @param in rpc 协议 入站数据 sub 部分
     * @param out 解码结果存储对象
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 注意程序的健壮性
        // 我们规定数据头部为 当前数据可用长度，先处理该数字
        if(in.readableBytes() < BODY_LENGTH) {
            return;
        }
        // ByteBuf 类似 goto 指令的标记
        // 再当前位置做记号，方便下次重置到当前位置
        in.markReaderIndex();
        int dataLength = in.readInt();
        // 排除不合理的 dataLength
        if(dataLength<0||in.readableBytes()<0) {
            log.error("输入的 dataLength 不合法，脏数据");
            return;
        }
        // 对于 长度不满足 dataLength的数据包，打回
        if(in.readableBytes()<dataLength) {
            in.resetReaderIndex();
            log.error("输入的数据长度异常");
            return;
        }
        byte[] body = new byte[dataLength];
        in.readBytes(body);
        Object obj = serializer.deSerialize(body, genericClass);
        out.add(obj);
        log.info("成功处理 Rpc 协议传输来的信息");
    }
}
