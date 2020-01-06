package com.khirye.rpc.client;

import com.khirye.rpc.transport.Transport;

public interface StubFactory {
    <T> T createStub(Transport transport, Class<T> serviceClass);
}
