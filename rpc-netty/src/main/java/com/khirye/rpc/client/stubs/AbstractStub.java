package com.khirye.rpc.client.stubs;

import com.khirye.rpc.client.RequestIdSupport;
import com.khirye.rpc.client.ServiceStub;
import com.khirye.rpc.serialize.SerializeSupport;
import com.khirye.rpc.transport.Transport;
import com.khirye.rpc.transport.command.Code;
import com.khirye.rpc.transport.command.Command;
import com.khirye.rpc.transport.command.Header;
import com.khirye.rpc.transport.command.ResponseHeader;
import lombok.Setter;

import java.util.concurrent.ExecutionException;

@Setter
public class AbstractStub implements ServiceStub {

    protected Transport transport;

    protected  byte[] invokeRemote(RpcRequest request) {
        Header header = new Header(0, 1, RequestIdSupport.next());
        byte[] payload = SerializeSupport.serialize(request);
        Command command = new Command(header, payload);

        try {
            Command responseCommand = transport.send(command).get();
            ResponseHeader responseHeader = (ResponseHeader)responseCommand.getHeader();
            if(responseHeader.getCode() == Code.SUCCESS.getCode()) {
                return responseCommand.getPayload();
            }
            throw new RuntimeException(responseHeader.getError());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
