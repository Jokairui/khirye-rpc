package com.khirye.rpc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

public interface TransportClient extends Closeable {
    @Override
    void close() throws IOException;

    Transport createTransport(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException;
}
