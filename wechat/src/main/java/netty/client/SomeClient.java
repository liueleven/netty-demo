package netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * @description: 客户端
 * @date: 2019-09-26 13:29
 * @author: 十一
 */
public class SomeClient {

    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LineBasedFrameDecoder(2048));
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new SomeClientHandler());
                    }
                });

        ChannelFuture future = bootstrap.connect("localhost", 8888).sync();
        // 获取键盘输入
        InputStreamReader is = new InputStreamReader(System.in, "UTF-8");
        BufferedReader br = new BufferedReader(is);
        // 将输入的内容写入到Channel
        future.channel().writeAndFlush(br.readLine() + "\r\n");
    }
}
