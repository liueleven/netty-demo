package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @description: 服务端
 * @date:        2019-09-26
 * @author:      十一
 */
public class SomeServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        NioEventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                     .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * @param maxFrameLength 该frame最大长度。如果长度超出抛异常 TooLongFrameException
                             * @param lengthFieldOffset 偏移量
                             * @param lengthFieldLength 长度
                             * @param lengthAdjustment  长度调整值
                             * @param initialBytesToStrip 从解码帧中去掉的第一个字节数
                             */
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024,
                                    0,4,0,4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // StringDecoder：字符串解码器，将Channel中的ByteBuf数据解码为String
                            pipeline.addLast(new StringDecoder());
                            // StringEncoder：字符串编码器，将String编码为将要发送到Channel中的ByteBuf
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new SomeServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(8888).sync();
            System.out.println("服务器已启动...");
            future.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
