package netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @description: 服务端处理器
 * @date:        2019-09-26
 * @author:      十一
 */
public class SomeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 空闲事件会触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evt1 = (IdleStateEvent) evt;
            String idleState = null;
            switch (evt1.state()) {
                case READER_IDLE: idleState = "读空闲超时"; break;
                case WRITER_IDLE: idleState = "写空闲超时"; break;
                default: ALL_IDLE: idleState = "读写空闲超时";
            }
            System.out.println(ctx.channel().remoteAddress() + ": " + idleState);
            // 释放资源
            ctx.channel().close();
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
