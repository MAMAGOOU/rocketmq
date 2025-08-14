package org.kangaroo.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.rocketmq.logging.ch.qos.logback.core.util.NetworkAddressUtil;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.kangaroo.rocketmq.common.ThreadFactoryImpl;
import org.kangaroo.rocketmq.common.constant.LoggerName;
import org.kangaroo.rocketmq.common.utils.NetworkUtil;

/**
 * @author dongpengkun
 * @className NettyRemotingServer
 * @date 2025/8/14
 * @desc Netty构建的Namesrv的服务端
 */
public class NettyRemotingServer {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);

    private final ServerBootstrap serverBootstrap;

    // 处理客户端IO事件的循环组
    private final EventLoopGroup eventLoopGroupSelector;

    // 处理客户端连接事件的循环组
    private final EventLoopGroup eventLoopGroupBoss;

    // Netty要使用的配置信息
    private final NettyServerConfig nettyServerConfig;

    public NettyRemotingServer() {
        this.serverBootstrap = new ServerBootstrap();
        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactoryImpl("NettyNIOBoss_"));
        this.eventLoopGroupSelector = new NioEventLoopGroup(2, new ThreadFactoryImpl("NettyServerNOPSelector_"));
        this.nettyServerConfig = new NettyServerConfig();
    }

    /**
     * @date 2025/8/14
     * @desc 启动Netty构建服务器的方法
     */
    public void start() throws InterruptedException {
        serverBootstrap.group(eventLoopGroupBoss, eventLoopGroupSelector)
                // 设置是否使用epoll，linux下会使用
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                // 还未想好
                .localAddress("localhost", 9876)
                // 为接受到的客户端channel设置handler处理器，即pipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        configChannel(socketChannel);
                    }
                });
        ChannelFuture sync = serverBootstrap.bind().sync();
    }

    // 判断是否使用epoll模式
    private boolean useEpoll() {
        // 这里可以根据实际情况判断是否使用epoll
        // todo NetworkUtil待实现
        NetworkUtil.isLinuxPlatform() && nettyServerConfig.isUseEpollNativeSelector() && Epoll.isAvailable();
        return true;
    }

    /**
     * @param socketChannel 服务端收到的客户端channel
     * @return 配置好的pipeline
     * @desc 配置服务端收到的客户端channel的方法
     */
    protected ChannelPipeline configChannel(SocketChannel socketChannel) {
        // todo 待实现
        return null;
    }
}
