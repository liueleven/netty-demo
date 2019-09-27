package netty.client;

import cn.eleven.netty.server.SomeService;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-26 22:55
 * @author: 十一
 */
public class RpcConsumer {

    public static void main(String[] args) {
        SomeService someService = RpcProxy.create(SomeService.class);
        System.out.println(someService.doSome("研发部"));
    }
}
