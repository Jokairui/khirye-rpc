package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.RequestHandler;
import com.khirye.rpc.transport.RequestHandlerRegistry;
import com.khirye.rpc.transport.command.Command;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class RequestInvocation extends SimpleChannelInboundHandler<Command> {

    private final RequestHandlerRegistry requestHandlerRegistry;
    public RequestInvocation(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command request) throws Exception {
        RequestHandler requestHandler = requestHandlerRegistry.get(request.getHeader().getType());

        if (null != requestHandler) {
            Command response = requestHandler.handle(request);

            channelHandlerContext.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.warn("Write response failed!", channelFuture.cause());
                    channelHandlerContext.channel().close();
                }
            });
        } else {
            throw new Exception(String.format("No handler for request with type: %d", request.getHeader().getType()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Exception: ", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
