package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.command.Command;
import com.khirye.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

public abstract class CommandEncoder extends MessageToByteEncoder<Command> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Command command, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(Integer.BYTES + command.getHeader().length() + command.getPayload().length);
        encodeHeader(command.getHeader(), byteBuf);
        byteBuf.writeBytes(command.getPayload());
    }

    protected abstract void encodeHeader(Header header, ByteBuf byteBuf);
}
