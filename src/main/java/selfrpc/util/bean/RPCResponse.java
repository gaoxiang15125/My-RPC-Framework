package selfrpc.util.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: MyRpcStudy
 * @description: Rpc 返回结果信息实体类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-19 11:18
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RPCResponse {
    String resultMessage;
}
