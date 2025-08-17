package org.kangaroo.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.kangaroo.protocol.RemotingCommand;
import org.kangaroo.rocketmq.common.constant.LoggerName;

/**
 * @author dongpengkun
 * @className NettyEncoder
 * @date 2025/8/17
 * @desc Netty构建Namesrv服务端的解码器
 */
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand, ByteBuf byteBuf) throws Exception {
        // todo 待实现
    }
}
