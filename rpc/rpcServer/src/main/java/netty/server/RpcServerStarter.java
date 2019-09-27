package netty.server;

import netty.server.RpcServer;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-26 22:39
 * @author: 十一
 */
public class RpcServerStarter {

    public static void main(String[] args) throws Exception {
        // 指定服务的包名
        new RpcServer().publish("netty.service");
    }
}
