package com.khirye.rpc.transport.command;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command {
    protected Header header;
    private byte[] payload;
}
