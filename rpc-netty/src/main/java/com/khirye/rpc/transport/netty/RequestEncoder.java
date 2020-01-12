package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.EventExecutorGroup;

public class RequestEncoder extends CommandEncoder {
    @Override
    protected void encodeHeader(Header header, ByteBuf byteBuf) {
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeInt(header.getRequestId());
    }
}
