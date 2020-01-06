package com.khirye.rpc;

import com.khirye.rpc.api.NameService;
import com.khirye.rpc.api.RpcAccessPoint;
import com.khirye.rpc.api.spi.ServiceSupport;
import com.khirye.rpc.client.StubFactory;
import com.khirye.rpc.transport.Transport;
import com.khirye.rpc.transport.TransportClient;

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
    public <T> URI addServiceProvider(Class<T> clazz) {
        return null;
    }

    @Override
    public NameService getNameService(URI uri) {
        return null;
    }

    @Override
    public Closeable startServer() throws Exception {
        return null;
    }
}
