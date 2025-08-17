package org.kangaroo.netty;


/**
 * @author dongpengkun
 * @className NettySystemConfig
 * @date 2025/8/17
 * @desc Netty系统配置类
 */
public class NettySystemConfig {

    public static final String COM_ROCKETMQ_REMOTING_SOCKET_SNDBUF_SIZE =
            "com.rocketmq.remoting.socket.sndbuf.size";
    public static final String COM_ROCKETMQ_REMOTING_SOCKET_RCVBUF_SIZE =
            "com.rocketmq.remoting.socket.rcvbuf.size";
    public static final String COM_ROCKETMQ_REMOTING_SOCKET_BACKLOG =
            "com.rocketmq.remoting.socket.backlog";
    public static final String COM_ROCKETMQ_REMOTING_WRITE_BUFFER_HIGH_WATER_MARK_VALUE =
            "com.rocketmq.remoting.write.buffer.high.water.mark";
    public static final String COM_ROCKETMQ_REMOTING_WRITE_BUFFER_LOW_WATER_MARK =
            "com.rocketmq.remoting.write.buffer.low.water.mark";

    public static int socketSndbufSize =
            Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_SOCKET_SNDBUF_SIZE, "0"));
    public static int socketRcvbufSize =
            Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_SOCKET_RCVBUF_SIZE, "0"));
    public static int writeBufferHighWaterMark =
            Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_WRITE_BUFFER_HIGH_WATER_MARK_VALUE, "0"));
    public static int writeBufferLowWaterMark =
            Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_WRITE_BUFFER_LOW_WATER_MARK, "0"));
    public static int socketBacklog =
            Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_SOCKET_BACKLOG, "1024"));
}
