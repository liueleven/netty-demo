package netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description: 服务端处理器
 * @date:        2019-09-26
 * @author:      十一
 */
public class SomeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将来自于客户端的数据显示在服务端控制台
        System.out.println(ctx.channel().remoteAddress() + "，" + msg);
        // 向客户端发送数据
        ctx.channel().writeAndFlush("from server：" + UUID.randomUUID());
        TimeUnit.MILLISECONDS.sleep(500);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
