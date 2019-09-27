package netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @description: 客户端处理器
 * SimpleChannelInboundHandler 支持泛型，可以对指定的消息类型进行处理
 * @date:        2019-09-26
 * @author:      十一
 */
public class SomeClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "，" + msg);
        ctx.channel().writeAndFlush("该消息来自 client：" + System.currentTimeMillis() / 1000);
        /**
         * client会定时ping一下服务端，发送心跳来保持连接
         * 这里我们设置随机阻塞时间，如果超过了server设置的心跳超时时间，那么服务端就会认为client断开了，然后关闭连接
         */
        TimeUnit.SECONDS.sleep(new Random().nextInt(10));
    }

    /**
     * 当Channel被激活后会触发该方法的执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush("from client：begin talking。。。");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
