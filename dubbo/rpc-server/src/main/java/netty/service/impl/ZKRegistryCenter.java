package netty.service.impl;

import netty.model.ZKConstant;
import netty.service.RegistryCenter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @description: 服务注册
 * @date: 2019-09-27 17:15
 * @author: 十一
 */
public class ZKRegistryCenter implements RegistryCenter {

    private CuratorFramework curatorFramework;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZKConstant.ZK_CLUSTER)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }

    /**
     * 服务注册
     * @param serviceName
     * @param serviceAddress
     */
    public void register(String serviceName, String serviceAddress) {
        String servicePath = ZKConstant.ZK_DUBBO_ROOT_PATH + "/" + serviceName;
        try {
            // 判断路径是否存在
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath, "0.".getBytes());
            }
            String addressPath = servicePath + "/" + serviceAddress;
            String hostNode = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(addressPath, "0".getBytes());
            System.out.println("Service Host Register Success!  " + hostNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
