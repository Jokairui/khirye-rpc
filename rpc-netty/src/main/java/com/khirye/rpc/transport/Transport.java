package com.khirye.rpc.transport;

import com.khirye.rpc.transport.command.Command;

import java.util.concurrent.CompletableFuture;

public interface Transport {

    CompletableFuture<Command> send(Command request);
}
