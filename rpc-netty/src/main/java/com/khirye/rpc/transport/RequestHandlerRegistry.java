package com.khirye.rpc.transport;

import com.khirye.rpc.api.spi.ServiceSupport;
import com.khirye.rpc.api.spi.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
public class RequestHandlerRegistry {

    private Map<Integer, RequestHandler> handlerMap = new HashMap();

    private static RequestHandlerRegistry requestHandlerRegistry;

    public static RequestHandlerRegistry getInstance() {
        if (requestHandlerRegistry == null) {
            return new RequestHandlerRegistry();
        }
        return requestHandlerRegistry;
    }
    private RequestHandlerRegistry() {
        Collection<RequestHandler> requestHandlers = ServiceSupport.loadAll(RequestHandler.class);
        requestHandlers.forEach(requestHandler -> {
            handlerMap.put(requestHandler.type(), requestHandler);
            log.info("Load request handler, type: {}, class: {}.", requestHandler.type(), requestHandler.getClass().getCanonicalName());
        });
    }

    public RequestHandler get(int type) {
        return handlerMap.get(type);
    }
}
