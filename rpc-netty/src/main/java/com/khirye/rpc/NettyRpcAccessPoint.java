package com.khirye.rpc;

import com.khirye.rpc.api.NameService;
import com.khirye.rpc.api.RpcAccessPoint;
import com.khirye.rpc.api.spi.ServiceSupport;
import com.khirye.rpc.client.StubFactory;
import com.khirye.rpc.server.ServiceProviderRegistry;
import com.khirye.rpc.transport.RequestHandlerRegistry;
import com.khirye.rpc.transport.Transport;
import com.khirye.rpc.transport.TransportClient;
import com.khirye.rpc.transport.TransportServer;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class NettyRpcAccessPoint implements RpcAccessPoint {

    private TransportClient client = ServiceSupport.load(TransportClient.class);
    private final Map<URI, Transport> clientMap = new ConcurrentHashMap<>();
    private final StubFactory stubFactory = ServiceSupport.load(StubFactory.class);

    private TransportServer server = null;
    private final int port = 9999;
    private final String host = "localhost";
    private final URI uri = URI.create("rpc://" + host + ":" + port);
    private final ServiceProviderRegistry serviceProviderRegistry = ServiceSupport.load(ServiceProviderRegistry.class);

    @Override
    public <T> T getRemoteService(URI serviceUri, Class<T> clazz) {
        Transport transport = clientMap.computeIfAbsent(serviceUri, this::createTransport);
        return stubFactory.createStub(transport, clazz);
    }

    private Transport createTransport(URI uri) {
        try {
            return client.createTransport(new InetSocketAddress(uri.getHost(), uri.getPort()), 30000L);
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> URI addServiceProvider(T service, Class<T> clazz) {
        serviceProviderRegistry.addServiceProvider(clazz, service);
        return uri;
    }

    @Override
    public NameService getNameService(URI uri) {
        return null;
    }

    @Override
    public synchronized Closeable startServer() throws Exception {
        if (null == server) {
            server = ServiceSupport.load(TransportServer.class);
            server.start(RequestHandlerRegistry.getInstance(), port);
        }
        return () -> {
            if (null != server) {
                server.stop();
            }
        };
    }
}
