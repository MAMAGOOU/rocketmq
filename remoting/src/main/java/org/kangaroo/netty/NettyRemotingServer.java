package org.kangaroo.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.rocketmq.logging.ch.qos.logback.core.util.NetworkAddressUtil;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.kangaroo.rocketmq.common.ThreadFactoryImpl;
import org.kangaroo.rocketmq.common.constant.LoggerName;
import org.kangaroo.rocketmq.common.utils.NetworkUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
    private NettyEncoder encoder;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;


    public NettyRemotingServer() {
        this.serverBootstrap = new ServerBootstrap();
        this.eventLoopGroupBoss = buildEventLoopGroupBoss();
        this.eventLoopGroupSelector = buildEventLoopGroupSelector();
        this.nettyServerConfig = new NettyServerConfig();
    }

    /**
     * 构建channel的IO事件循环组
     *
     * @return
     */
    private EventLoopGroup buildEventLoopGroupSelector() {
        if (useEpoll()) {
            return new EpollEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactoryImpl("NettyEpollSelector_"));
        } else {// 如果不启用则创建NIO的事件循环组，事件循环组使用的线程数量从配置信息类中获取
            return new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactoryImpl("NettyNIOSelector_"));
        }
    }

    /**
     * 处理客户端连接事件的IO循环组
     *
     * @return
     */
    private EventLoopGroup buildEventLoopGroupBoss() {
        if (useEpoll()) {
            return new EpollEventLoopGroup(1, new ThreadFactoryImpl("NettyEpollBoss_"));
        } else {
            return new NioEventLoopGroup(1, new ThreadFactoryImpl("NettyNIOBoss_"));
        }
    }

    // 判断是否使用epoll模式
    private boolean useEpoll() {
        // 这里可以根据实际情况判断是否使用epoll
        return NetworkUtil.isLinuxPlatform() &&
                nettyServerConfig.isUseEpollNativeSelector() &&
                Epoll.isAvailable();
    }

    /**
     * 为Namesrv服务端接受的channel准备可共享的handler处理器方法
     */
    private void prepareSharableHandlers() {
        // 创建编码器
        encoder = new NettyEncoder();
    }

    /**
     * @date 2025/8/14
     * @desc 启动Netty构建服务器的方法
     */
    public void start() throws InterruptedException {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactoryImpl("NettyServerCodeThread_"));

        prepareSharableHandlers();

        serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                // 设置是否使用epoll，linux下会使用
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                // 设置接受客户端连接队列的大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 设置服务器监听地址可重用标志
                .option(ChannelOption.SO_REUSEADDR, true)
                // 设置启动TCP探活机制
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                // 禁用nagle算法，避免出现发送数据包过小而合并数据包
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 服务端监听的IP和PORT
                .localAddress(new InetSocketAddress(this.nettyServerConfig.getBindAddress(), this.nettyServerConfig.getListenPort()))
                // 为接受到的客户端channel设置handler处理器，即pipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        configChannel(socketChannel);
                    }
                });

        // 设置服务器自定义信息
        addCustomConfig(serverBootstrap);

        ChannelFuture sync = serverBootstrap.bind().sync();
        InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
        // 判断Netty配置信息对象的监听端口号是否更新，如果没有更新，则将真正监听的端口号更新到配置信息对象中
        if (0 == nettyServerConfig.getListenPort()) {
            this.nettyServerConfig.setListenPort(addr.getPort());
        }

        // 记录Netty服务器启动日志
        log.info("RemotingServer stated, listening {}:{}", this.nettyServerConfig.getBindAddress(), this.nettyServerConfig.getListenPort());
    }


    /**
     * @param socketChannel 服务端收到的客户端channel
     * @return 配置好的pipeline
     * @desc 配置服务端收到的客户端channel的方法
     */
    protected ChannelPipeline configChannel(SocketChannel socketChannel) {
        // 设置空闲处理器，从配置对象中获取channel最大的空闲时间
        return socketChannel.pipeline()
                .addLast(defaultEventExecutorGroup,
                        encoder,
                        new NettyDecoder(),
                        // 设置空闲处理器，从配置对象中获取channel最大的空闲时间
                        new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()));
    }

    /**
     * 服务器设置用户自定义信息
     *
     * @param channelHandler
     */
    private void addCustomConfig(ServerBootstrap channelHandler) {
        // 设置服务器发送缓冲区大小
        if (nettyServerConfig.getServerSocketSndBufSize() > 0) {
            log.info("server set SO_SNDBUF to {}", nettyServerConfig.getServerSocketSndBufSize());
            channelHandler.childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize());
        }
        // 设置服务器接受缓冲区大小
        // 设置写缓冲区的高水位和低水位线
        // 判断是否开启ByteBuf池化功能，如果开启就设置到Netty中
    }

    /**
     * 终止Namesrv服务器的方法
     */
    public void shutdown() {
        // todo
    }
}
