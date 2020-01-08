package com.khirye.rpc.api;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

public interface NameService {

    void register(String serviceName, URI uri) throws IOException;

    URI lookupService(String serviceName) throws IOException;

    void connect(URI serviceUri);

    Collection<String> supportedSchemes();
}
