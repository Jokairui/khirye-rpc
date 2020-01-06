package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;

public class RequestDecoder extends CommandDecoder {
    @Override
    protected Header decodeHeader(ByteBuf byteBuf) {
        return new Header(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt());
    }
}
