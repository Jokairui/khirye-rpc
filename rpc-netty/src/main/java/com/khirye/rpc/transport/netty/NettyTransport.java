package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.InFlightRequests;
import com.khirye.rpc.transport.ResponseFuture;
import com.khirye.rpc.transport.Transport;
import com.khirye.rpc.transport.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class NettyTransport implements Transport {

    private final Channel channel;
    private final InFlightRequests inFlightRequests;

    public NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = channel;
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    public CompletableFuture<Command> send(Command request) {
        CompletableFuture<Command> completableFuture = new CompletableFuture<>();

        try {
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(), completableFuture));

            channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    completableFuture.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Exception e) {
            inFlightRequests.remove(request.getHeader().getRequestId());
            completableFuture.completeExceptionally(e);
        }

        return completableFuture;
    }
}
