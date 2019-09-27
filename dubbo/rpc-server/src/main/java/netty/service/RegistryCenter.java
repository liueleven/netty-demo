package netty.service;

/**
 * @description: 注册中心，提供服务端注册功能
 * @date: 2019-09-27 17:14
 * @author: 十一
 */
public interface RegistryCenter {

    void register(String serviceName,String serviceAddress);
}
