package com.khirye.rpc.client.stubs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RpcRequest {

    private final String interfaceName;
    private final String methodName;
    private final byte[] serializedArguments;

}
