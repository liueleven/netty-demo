package netty.client;

import cn.eleven.netty.model.InvokeMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @description: 代理类增强实现
 * @date: 2019-09-26 22:42
 * @author: 十一
 */
public class RpcProxy {


    public static <T> T create(final Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass().equals(Object.class)) {
                    return method.invoke(this,args);
                }
                return rpcInvoke(clazz,method,args);
            }
        });
    }

    private static Object rpcInvoke(Class<?> clazz, Method method, Object[] args) {
        final RpcClientHandler rpcClientHandler = new RpcClientHandler();

        NioEventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerLoopGroup)
                .channel(NioSocketChannel.class)
                // Nagle算法
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(rpcClientHandler);
                    }
                });
        // 同步连接
       try {
           ChannelFuture future = bootstrap.connect("localhost", 8888).sync();
           InvokeMessage invokeMessage = new InvokeMessage();
           invokeMessage.setClassName(clazz.getName());
           invokeMessage.setMethodName(method.getName());
           invokeMessage.setParamTypes(method.getParameterTypes());
           invokeMessage.setParamValues((args));
           future.channel().writeAndFlush(invokeMessage).sync();
           future.channel().closeFuture().sync();
       }catch (Exception e) {
           e.printStackTrace();
       }finally {
           if (workerLoopGroup != null) {
               workerLoopGroup.shutdownGracefully();
           }
       }

       return rpcClientHandler.getResult();


    }


}
