package com.khirye.rpc.nameservice;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

public class Metadata extends HashMap<String, List<URI>> {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Metadata:").append("\n");
        for (Entry<String, List<URI>> entry : entrySet()) {
            sb.append("\t").append("Classname: ")
                .append(entry.getKey()).append("\n")
                .append("\t").append("URIs:").append("\n");
            for(URI uri : entry.getValue()) {
                sb.append("\t\t").append(uri).append("\n");
            }
        }
        return sb.toString();
    }
}
