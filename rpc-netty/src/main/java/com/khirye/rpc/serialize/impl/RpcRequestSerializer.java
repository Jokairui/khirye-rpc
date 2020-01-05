package com.khirye.rpc.serialize.impl;

import com.khirye.rpc.client.stubs.RpcRequest;
import com.khirye.rpc.serialize.Serializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RpcRequestSerializer implements Serializer<RpcRequest> {
    @Override
    public int size(RpcRequest entry) {
        return Integer.BYTES + entry.getInterfaceName().getBytes(StandardCharsets.UTF_8).length +
            Integer.BYTES + entry.getMethodName().getBytes(StandardCharsets.UTF_8).length +
            Integer.BYTES + entry.getSerializedArguments().length;
    }

    @Override
    public void serialize(RpcRequest entry, byte[] bytes, int offset, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, offset, length);
        byte[] tmpBytes = entry.getInterfaceName().getBytes(StandardCharsets.UTF_8);
        byteBuffer.putInt(tmpBytes.length);
        byteBuffer.put(tmpBytes);

        tmpBytes = entry.getMethodName().getBytes(StandardCharsets.UTF_8);
        byteBuffer.putInt(tmpBytes.length);
        byteBuffer.put(tmpBytes);

        tmpBytes = entry.getSerializedArguments();
        byteBuffer.putInt(tmpBytes.length);
        byteBuffer.put(tmpBytes);
    }

    @Override
    public RpcRequest parse(byte[] bytes, int offset, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, offset, length);
        int len = byteBuffer.getInt();
        byte[] tmpBytes = new byte[len];
        byteBuffer.get(tmpBytes);
        String interfaceName = new String(tmpBytes, StandardCharsets.UTF_8);

        len = byteBuffer.getInt();
        tmpBytes = new byte[len];
        byteBuffer.get(tmpBytes);
        String methodName = new String(tmpBytes, StandardCharsets.UTF_8);

        len = byteBuffer.getInt();
        tmpBytes = new byte[len];
        byteBuffer.get(tmpBytes);

        return new RpcRequest(interfaceName, methodName, tmpBytes);
    }

    @Override
    public byte type() {
        return Types.TYPE_RPC_REQUEST;
    }

    @Override
    public Class<RpcRequest> getSerializeClass() {
        return RpcRequest.class;
    }
}
