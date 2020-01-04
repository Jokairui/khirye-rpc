package com.khirye.rpc.api;

import java.net.URI;
import java.util.Collection;

public interface NameService {

    void register(String serviceName);

    URI lookupService(String serviceName);

    void connect(URI serviceUri);

    Collection<String> supportedSchemes();
}
