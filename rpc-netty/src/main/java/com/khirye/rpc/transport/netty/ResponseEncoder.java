package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.command.Header;
import com.khirye.rpc.transport.command.ResponseHeader;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class ResponseEncoder extends CommandEncoder {
    @Override
    protected void encodeHeader(Header header, ByteBuf byteBuf) {
        ResponseHeader responseHeader = (ResponseHeader) header;

        byteBuf.writeInt(responseHeader.getType());
        byteBuf.writeInt(responseHeader.getVersion());
        byteBuf.writeInt(responseHeader.getRequestId());
        byteBuf.writeInt(responseHeader.getCode());
        byte[] errorByte = responseHeader.getError() == null ? new byte[0] : responseHeader.getError().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(errorByte.length);
        byteBuf.writeBytes(errorByte);
    }
}
