package com.khirye.rpc.transport;

import com.khirye.rpc.transport.command.Command;

public interface RequestHandler {

    Command handle(Command request);

    int type();
}
