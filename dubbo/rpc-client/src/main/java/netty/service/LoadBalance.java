package netty.service;

import java.util.List;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-27 17:48
 * @author: 十一
 */
public interface LoadBalance {

    String choose(List<String> servers);
}
