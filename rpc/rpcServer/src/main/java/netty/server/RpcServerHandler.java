package netty.server;

import cn.eleven.netty.model.InvokeMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

/**
 * @description: rpc 处理器
 * @date: 2019-09-26 22:33
 * @author: 十一
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<InvokeMessage> {

    private Map<String,Object> registryMap;

    public RpcServerHandler(Map<String, Object> registryMap) {
        this.registryMap = registryMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InvokeMessage msg) throws Exception {
        Object result = "没有指定的提供者";
        // 判断注册表中是否有指定的服务
        if(registryMap.containsKey(msg.getClassName())) {
            // 获取提供者实例
            Object provider = registryMap.get(msg.getClassName());
            // 进行方法执行
            result = provider.getClass()
                    .getMethod(msg.getMethodName(), msg.getParamTypes())
                    .invoke(provider, msg.getParamValues());
        }
        // 将调用结果发送给客户端
        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
