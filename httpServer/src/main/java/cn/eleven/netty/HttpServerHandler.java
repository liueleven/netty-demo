package cn.eleven.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Random;

/**
 * @description: 自定义服务器处理器
 * @date: 2019-09-26 13:47
 * @author: 十一
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private String[] colors = {"red", "black", "orange", "green"};

    private Random random = new Random();

    /**
     * 通道建立连接
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("通道建立连接。。。");

    }



    /**
     * 从Channel上获取数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            System.out.println("请求方式：" + request.method().name());
            System.out.println("请求URI：" + request.uri());

            ByteBuf content = Unpooled.copiedBuffer("<h1 style=\"color: " + getColor() + "\">Hello Netty!</h1>", CharsetUtil.UTF_8);
            DefaultHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    content);
//            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"h/plain");
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            // 添加监听器，响应体发送完毕则直接将Channel关闭
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        }

    }

    private String getColor() {
        return colors[random.nextInt(colors.length)];
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
