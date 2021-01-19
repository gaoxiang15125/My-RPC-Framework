package selfrpc.util.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: MyRpcStudy
 * @description: Rpc 请求信息实体类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 11:17
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RPCRequest {
    // 使用自定义序列化工具，不再继承 serializer
    String interfaceName;
    String methodName;
}
