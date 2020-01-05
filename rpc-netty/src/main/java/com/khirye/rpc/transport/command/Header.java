package com.khirye.rpc.transport.command;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Header {

    private int type;
    private int version;
    private int requestId;

    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES;
    }
}
