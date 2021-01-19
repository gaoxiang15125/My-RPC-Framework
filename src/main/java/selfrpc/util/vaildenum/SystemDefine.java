package selfrpc.util.vaildenum;

import io.netty.util.AttributeKey;
import selfrpc.util.bean.RPCResponse;

/**
 * @program: MyRpcStudy
 * @description: 系统常量存储类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 17:52
 **/
public class SystemDefine {

    // 客户端存储 Response 信息，标志字符串
    public static String CLIENT_RESPONSE_KEY = "rpcResponse";

    // 客户端存储 Response 信息的 key 实例
    public static AttributeKey<RPCResponse> CLIENT_ATTRIBUTE_KEY = AttributeKey.valueOf("rpcResponse");
}
