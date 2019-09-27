package netty;


import netty.server.SomeService;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-26 22:55
 * @author: 十一
 */
public class RpcConsumer {

    public static void main(String[] args) {
        SomeService someService = RpcProxy.create(SomeService.class);
        if (someService != null) {
            System.out.println(someService.doSome("研发部"));
        }
    }
}
