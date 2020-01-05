package com.khirye.rpc.client;

import com.khirye.rpc.transport.Transport;

public interface ServiceStub {
    void setTransport(Transport transport);
}
