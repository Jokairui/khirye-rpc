package com.khirye.rpc.client;

import com.khirye.rpc.transport.Transport;

public interface StubFactory {
    <T> T createStud(Transport transport, Class<T> serviceClass);
}
