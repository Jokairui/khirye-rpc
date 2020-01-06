package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.command.Header;
import com.khirye.rpc.transport.command.ResponseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;

public class ResponseDecoder extends CommandDecoder {
    @Override
    protected Header decodeHeader(ByteBuf byteBuf) {
        int type = byteBuf.readInt();
        int version = byteBuf.readInt();
        int requestId = byteBuf.readInt();
        int code = byteBuf.readInt();
        int errorLength = byteBuf.readInt();
        byte[] errorByte = new byte[errorLength];
        byteBuf.readBytes(errorByte);
        String error = new String(errorByte, StandardCharsets.UTF_8);
        return new ResponseHeader(type, version, requestId, code, error);
    }
}
