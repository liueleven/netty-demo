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
import netty.service.RegistryCenter;

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

    /**
     * 保存所有的类名
     */
    private List<String> classCache = new ArrayList<String>();

    /**
     *
     * @param providerPackage 要扫描的包路径
     * @param registryCenter 注册中心
     * @param serviceAddress 需要注册的服务地址，格式：ip:port
     * @throws Exception
     */
    public void publish(String providerPackage, RegistryCenter registryCenter, String serviceAddress) throws Exception {
        // 扫描包路径，缓存类名
        getProviderClass(providerPackage);
        // 服务注册
        doRegister(registryCenter,serviceAddress);

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

        String ip = serviceAddress.split(":")[0];
        String port = serviceAddress.split(":")[1];
        try {
            ChannelFuture future = bootstrap.bind(ip,Integer.valueOf(port)).sync();
            System.out.println("微服务注册成功，ip:" + ip +", port：" + port);
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
    private void doRegister(RegistryCenter registryCenter,String serviceAddress) throws Exception {
        if (classCache.size() == 0) {
            return;
        }
        boolean isRegisted = false;
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            // interfaceName的值：cn.eleven.netty.server.SomeService
            Class<?>[] interfaces = clazz.getInterfaces();
            String interfaceName = interfaces[0].getName();
            registerMap.put(interfaceName,clazz.newInstance());
            if (!isRegisted) {
                registryCenter.register(interfaceName,serviceAddress);
                isRegisted = true;
            }
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
                // 保存类名，netty.service.impl.SomeServiceImpl
                classCache.add(providerPackage + "." +fileName);
            }

        }

    }

}
