package com.khirye.rpc.server;

import com.khirye.rpc.api.spi.Singleton;
import com.khirye.rpc.client.ServiceTypes;
import com.khirye.rpc.client.stubs.RpcRequest;
import com.khirye.rpc.serialize.SerializeSupport;
import com.khirye.rpc.transport.RequestHandler;
import com.khirye.rpc.transport.command.Code;
import com.khirye.rpc.transport.command.Command;
import com.khirye.rpc.transport.command.Header;
import com.khirye.rpc.transport.command.ResponseHeader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Singleton
@Slf4j
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {

    private Map<String, Object> serviceProviders = new HashMap<>();

    @Override
    public synchronized <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
        log.info("Add service: {}, provider: {}", serviceClass.getCanonicalName(), serviceProvider.getClass().getCanonicalName());
    }

    @Override
    public Command handle(Command request) {
        Header header = request.getHeader();
        RpcRequest rpcRequest = SerializeSupport.parse(request.getPayload());
        try {
            Object serviceProvider = serviceProviders.get(rpcRequest.getInterfaceName());
            if (serviceProvider != null) {
                String arg = SerializeSupport.parse(rpcRequest.getSerializedArguments());
                Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
                String result = (String) method.invoke(serviceProvider, arg);
                return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId()), SerializeSupport.serialize(result));
            }
            log.warn("No service provider of {}#{}(String)!", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.NO_PROVIDER.getCode(), "No provider!"), new byte[0]);
        } catch (Exception e) {
            log.warn("Exception: ", e);
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.UNKNOWN_ERROR.getCode(), e.getMessage()), new byte[0]);
        }
    }

    @Override
    public int type() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }
}
