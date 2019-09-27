package netty.service;

import cn.eleven.netty.server.SomeService;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-26 22:11
 * @author: 十一
 */
public class SomeServiceImpl implements SomeService {

    @Override
    public String doSome(String depart) {
        return depart + " 欢迎你";
    }
}
