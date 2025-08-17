package org.kangaroo.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.kangaroo.rocketmq.common.constant.LoggerName;

/**
 * @author dongpengkun
 * @className NettyDecoder
 * @date 2025/8/17
 * @desc Netty构建Namesrv服务端的编码器
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);
    private static final int FRAME_MAX_LENGTH = Integer.parseInt(System.getProperty("com.rocketmq.remoting.frameMaxLength", "16777216"));


    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 0);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return null;
    }
}
