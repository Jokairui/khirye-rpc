package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.InFlightRequests;
import com.khirye.rpc.transport.Transport;
import com.khirye.rpc.transport.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class NettyClient implements TransportClient {

    private final InFlightRequests inFlightRequests;
    private Bootstrap bootstrap;
    private EventLoopGroup ioEventGroup;

    private List<Channel> channels = new LinkedList<>();

    public NettyClient() {
        inFlightRequests = new InFlightRequests();
    }

    @Override
    public void close() throws IOException {
        channels.forEach(channel -> {
            if (channel != null) {
                channel.close();
            }
        });

        if (ioEventGroup != null) {
            ioEventGroup.shutdownGracefully();
        }

        inFlightRequests.close();
    }

    @Override
    public Transport createTransport(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        return new NettyTransport(createChannel(address, connectionTimeout), inFlightRequests);
    }

    private synchronized Channel createChannel(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        if (address == null) {
            throw new IllegalArgumentException("address can't be null");
        }
        if (ioEventGroup == null) {
            ioEventGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        }
        if (bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(ioEventGroup)
                .handler(new ChannelInitializer() {

                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                            .addLast(new ResponseDecoder())
                            .addLast(new RequestEncoder())
                            .addLast(new ResponseInvocation(inFlightRequests));
                    }
                })
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        return getChannel(address, connectionTimeout);
    }

    private Channel getChannel(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        ChannelFuture channelFuture = bootstrap.connect(address);
        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }
        Channel channel = channelFuture.channel();

        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }

        //收集所有的channel，方便统一close
        channels.add(channel);
        return channel;
    }
}
