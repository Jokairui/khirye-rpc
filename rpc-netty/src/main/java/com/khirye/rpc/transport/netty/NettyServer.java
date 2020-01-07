package com.khirye.rpc.transport.netty;

import com.khirye.rpc.transport.RequestHandlerRegistry;
import com.khirye.rpc.transport.TransportServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer implements TransportServer {
    private Channel channel;
    EventLoopGroup acceptEventGroup;
    EventLoopGroup ioEventGroup;
    @Override
    public void start(RequestHandlerRegistry requestHandlerRegistry, int port) throws Exception {

        this.acceptEventGroup = newEventGroup();
        this.ioEventGroup = newEventGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
            .group(acceptEventGroup, ioEventGroup)
            .childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new RequestDecoder())
                        .addLast(new ResponseEncoder())
                        .addLast(new RequestInvocation(requestHandlerRegistry));
                }
            })
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        this.channel = serverBootstrap.bind(port)
            .sync()
            .channel();

    }

    private EventLoopGroup newEventGroup() {
        return Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    @Override
    public void stop() {
        if (this.acceptEventGroup != null) {
            this.acceptEventGroup.shutdownGracefully();
        }

        if (this.ioEventGroup != null) {
            this.ioEventGroup.shutdownGracefully();
        }

        if (this.channel != null) {
            this.channel.close();
        }
    }
}
