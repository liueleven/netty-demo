package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-26 22:18
 * @author: 十一
 */
public class RpcServer {

    private Map<String,Object> registerMap = new HashMap<String, Object>();

    private List<String> classCache = new ArrayList<String>();

    public void publish(String providerPackage) throws Exception {
        getProviderClass(providerPackage);
        doRegister();

        NioEventLoopGroup parentLoopGroup = new NioEventLoopGroup();
        NioEventLoopGroup childLoopGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(parentLoopGroup,childLoopGroup)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new RpcServerHandler(registerMap));
                    }
                });
        int port = 8888;
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("微服务注册成功，端口号：" + port);
            future.channel().closeFuture().sync();
        }catch (Exception e) {

        }finally {
            parentLoopGroup.shutdownGracefully();
            childLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 遍历classCache，获取到实现类所实现的接口名称，及创建该实现类对应实例
     * @throws Exception
     */
    private void doRegister() throws Exception {
        if (classCache.size() == 0) {
            return;
        }
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            // interfaceName的值：cn.eleven.netty.server.SomeService
            String interfaceName = clazz.getInterfaces()[0].getName();
            registerMap.put(interfaceName,clazz.newInstance());
        }
    }

    private void getProviderClass(String providerPackage) {
        System.out.println("path：" + providerPackage);
        // 获取class路径，file:/own/code/netty/kaikeba/demo/rpc/rpcServer/target/classes/netty/service
        URL resource = this.getClass().getClassLoader()
                .getResource(providerPackage.replaceAll("\\.","/"));
        File dir = new File(resource.getFile());

        for (File file : dir.listFiles()) {

            if (file.isDirectory()) {
                // 若当前文件为目录，则递归
                getProviderClass(providerPackage + "." + file.getName());
            }else if (file.getName().endsWith(".class")) {
                String fileName = file.getName().replace(".class", "").trim();
                // 保存类名，netty.service.SomeServiceImpl
                classCache.add(providerPackage + "." +fileName);
            }

        }

    }

}
