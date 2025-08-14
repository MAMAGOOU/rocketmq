package org.kangaroo.netty;


/**
 * @author dongpengkun
 * @className NettyServerConfig
 * @date 2025/8/15
 * @desc 封装Netty服务器要使用的所有配置
 */
public class NettyServerConfig implements Cloneable {

    private boolean useEpollNativeSelector = false;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (NettyServerConfig) super.clone();
    }

    public boolean isUseEpollNativeSelector() {
        return useEpollNativeSelector;
    }
}
