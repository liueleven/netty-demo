package netty.server;

import netty.service.impl.ZKRegistryCenter;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-26 22:39
 * @author: 十一
 */
public class RpcServerStarter {

    public static void main(String[] args) throws Exception {
        ZKRegistryCenter zkRegistryCenter = new ZKRegistryCenter();
        String serviceAddress = "127.0.0.1:8886";
        String providerPackage = "netty.service.impl";
        // 指定服务的包名
        new RpcServer().publish(providerPackage,zkRegistryCenter,serviceAddress);
    }
}
