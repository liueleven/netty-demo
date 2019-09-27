package netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description: 服务端处理器
 * @date:        2019-09-26
 * @author:      十一
 */
public class SomeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将来自于客户端的数据显示在服务端控制台
        System.out.println(ctx.channel().remoteAddress() + "，" + msg);
        // 向客户端发送数据
        ctx.channel().writeAndFlush("该消息来自 from server：" + UUID.randomUUID());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evt1 = (IdleStateEvent) evt;
            System.out.println("超时状态：" + evt1.state());
            if (evt1.state() == IdleState.READER_IDLE) {
                System.out.println("=====读取超时，关闭连接============");
                ctx.disconnect();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
