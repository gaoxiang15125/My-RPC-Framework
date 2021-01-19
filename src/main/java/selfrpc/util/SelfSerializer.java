package selfrpc.util;

/**
 * @program: MyRpcStudy
 * @description: 自定义序列化接口类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 11:11
 **/
public interface SelfSerializer {

    /**
     * 序列化
     * @param obj 需要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 序列化 字节数组
     * @param clazz 类
     * @param <T>
     * @return 反序列化对象
     */
    <T> T deSerialize(byte[] bytes, Class<T> clazz);
}
