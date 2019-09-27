package cn.eleven.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @description: 服务端
 * @date: 2019-09-26 13:29
 * @author: 十一
 */
public class HttpServer {

    public static void main(String[] args) {
        // 用来处理连接
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        // 用来处理连接后的操作
        EventLoopGroup childGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(parentGroup,childGroup)
                // 添加Channel类型
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 添加http编解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 添加自定义处理器
                        pipeline.addLast(new HttpServerHandler());
                    }
                });
        try {
            // 绑定端口
            ChannelFuture future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e) {

        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }

    }
}
