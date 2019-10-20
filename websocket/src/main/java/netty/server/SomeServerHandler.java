package netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: 服务端处理器
 * @date:        2019-09-26
 * @author:      十一
 */
public class SomeServerHandler extends ChannelInboundHandlerAdapter {

    ScheduledThreadPoolExecutor exec = null;
    Random random = new Random();


    /**
     * 创建一个ChannelGroup，其是一个线程安全的集合，其中存放着与当前服务器相连接的所有Active状态的Channel
     * GlobalEventExecutor是一个单例、单线程的EventExecutor，是为了保证对当前group中的所有Channel的处理线程是同一个线程
     *
     * ChannelGroup 用来维护当前处于active的channel，Inactive的channel会自动剔除
     *
     */
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     *
     * @param ctx
     * @param msg 客户端发来的信息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // todo 获取客户端的信息
    }

    /**
     * 只要有客户端Channel与服务端连接成功就会执行这个方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 获取到当前与服务器连接成功的channel
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "---上线");
        group.writeAndFlush(channel.remoteAddress() + "---上线\n");
        // 将当前channel添加到group中
        group.add(channel);

        if (exec == null) {
            exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    System.out.println("<=== 定时任务，模拟推送" + System.currentTimeMillis()/1000);
                    // 向所有的通道推送消息
                    group.writeAndFlush(new TextWebSocketFrame(currentData()));
                }
            },1000,2000, TimeUnit.MILLISECONDS);
        }
    }

    private ByteBuf currentData() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(String.valueOf(random.nextInt(100)).getBytes());
        return byteBuf;
    }


    /**
     * 只要有客户端Channel断开与服务端的连接就会执行这个方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 获取到当前要断开连接的Channel
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "----- 下线，当前在线人数：" + group.size() + "\n");

        // group中存放的都是Active状态的Channel，一旦某Channel的状态不再是Active，
        // group会自动将其从集合中踢出，所以，下面的语句不用写
        // remove()方法的应用场景是，将一个Active状态的channel移出group时使用
        // group.remove(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
