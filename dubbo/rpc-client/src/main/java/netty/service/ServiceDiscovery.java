package netty.service;

/**
 * @description: 一定要写注释啊
 * @date: 2019-09-27 17:49
 * @author: 十一
 */
public interface ServiceDiscovery {
    /**
     *
     * @param serviceName
     * @return 返回主机信息，格式：ip:port
     */
    String discover(String serviceName);
}
