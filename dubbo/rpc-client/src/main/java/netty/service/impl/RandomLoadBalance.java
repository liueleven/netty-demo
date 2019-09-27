package netty.service.impl;

import netty.service.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-27 17:48
 * @author: 十一
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String choose(List<String> servers) {
        int index = new Random().nextInt(servers.size());
        return servers.get(index);
    }
}
