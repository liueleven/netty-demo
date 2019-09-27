package netty.service.impl;

import netty.model.ZKConstant;
import netty.service.ServiceDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: zk 服务发现
 * @date: 2019-09-27 18:13
 * @author: 十一
 */
public class ServiceDiscoveryImpl implements ServiceDiscovery {

    List<String> servers = new ArrayList<String>();

    private CuratorFramework curator;

    public ServiceDiscoveryImpl() {
        this.curator = CuratorFrameworkFactory.builder()
                .connectString(ZKConstant.ZK_CLUSTER)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curator.start();
    }
    @Override
    public String discover(String serviceName) {
        try {
            String servicePath = ZKConstant.ZK_DUBBO_ROOT_PATH + "/" + serviceName;
            servers = curator.getChildren().forPath(servicePath);
            if (servers.size() == 0) {
                return null;
            }
            registerWatch(servicePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RandomLoadBalance().choose(servers);
    }

    /**
     * 注册一个监听器
     * @param servicePath
     * @throws Exception
     */
    private void registerWatch(final String servicePath) throws Exception{
        PathChildrenCache childrenCache = new PathChildrenCache(curator, servicePath, true);
        PathChildrenCacheListener cacheListener = (client, event) -> {
            servers = client.getChildren().forPath(servicePath);
            System.out.println("服务数量：" + servers.size());
        };
        childrenCache.getListenable().addListener(cacheListener);
        childrenCache.start();
    }

}
