package selfrpc.util.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import selfrpc.util.SelfSerializer;
import selfrpc.util.bean.RPCRequest;
import selfrpc.util.bean.RPCResponse;
import selfrpc.util.vaildenum.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * @program: MyRpcStudy
 * @description: Kryo 序列化实现工具
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 11:14
 **/
@Slf4j
public class KryoSerializer implements SelfSerializer {

    /**
     * Kryo 不是线程安全的 每个线程都有自己的 Kryo、input、output 实例
     * 因此 使用 ThreadLocal 存放 Kryo 对象
     * 潜在的问题： 需要无参构造方法，应该是序列化实现问题
     * 因为 kryo 不会受语言限制，标准理应更强
     * @param obj 需要序列化的对象
     * @return
     */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        // 因为处理序列化的时候用到了 字节流，需要考虑线程安全问题也是理所当然的
        Kryo kryo = new Kryo();
        kryo.register(RPCRequest.class);
        kryo.register(RPCResponse.class);
        // 是否关闭注册行为
        kryo.setReferences(true);
        // 是否关闭循环引用，以提高性能
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // Object -> byte: 将对象序列化为 byte 数组
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch(Exception e) {
            throw new SerializeException("序列化过程中出现错误: " + e);
        }
//        return new byte[0];
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new SerializeException("反序列化过程出错: " + e);
        }
    }

}