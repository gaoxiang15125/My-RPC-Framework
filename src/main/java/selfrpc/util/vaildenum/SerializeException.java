package selfrpc.util.vaildenum;

/**
 * @program: MyRpcStudy
 * @description: 序列化异常信息
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 12:22
 **/
public class SerializeException extends RuntimeException {

    public SerializeException(String message) {
        super(message);
    }
}
