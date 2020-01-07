package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.InFlightRequests;
import com.khirye.rpc.transport.ResponseFuture;
import com.khirye.rpc.transport.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseInvocation extends SimpleChannelInboundHandler<Command> {
    private final InFlightRequests inFlightRequests;

    public ResponseInvocation(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
        ResponseFuture responseFuture = inFlightRequests.remove(command.getHeader().getRequestId());

        if(responseFuture != null) {
            responseFuture.getFuture().complete(command);
        } else {
            log.warn("Dropped response: {}", command);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("exception caught: {}", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive())ctx.close();
    }
}
