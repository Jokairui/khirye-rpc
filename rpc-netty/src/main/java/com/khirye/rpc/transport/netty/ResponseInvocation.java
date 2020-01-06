package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.InFlightRequests;
import com.khirye.rpc.transport.command.Command;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

public class ResponseInvocation extends SimpleChannelInboundHandler<Command> {
    public ResponseInvocation(InFlightRequests inFlightRequests) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {

    }
}
