package com.khirye.rpc.api.spi;

import com.khirye.rpc.api.RpcAccessPoint;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceSupport {

    private final static Map<String, Object> singletonServices = new HashMap<>();

    public synchronized static <S> S load(Class<S> service) {
        return StreamSupport
            .stream(ServiceLoader.load(service).spliterator(), false)
            .map(ServiceSupport::singletonFilter)
            .findFirst().orElseThrow(ServiceLoaderException::new);
    }

    public synchronized static <S> Collection<S> loadAll(Class<S> service) {
        return StreamSupport
            .stream(ServiceLoader.load(service).spliterator(), false)
            .map(ServiceSupport::singletonFilter)
            .collect(Collectors.toList());
    }

    private static <S> S singletonFilter(S service) {

        if(service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }
}
