package com.khirye.rpc.api;

import java.io.Closeable;
import java.net.URI;

public interface RpcAccessPoint {

    <T> T getRemoteService(URI serviceUri, Class<T> clazz);

    <T> URI addServiceProvider(Class<T> clazz);

    NameService getNameService(URI uri);

    Closeable startServer() throws Exception;
}
