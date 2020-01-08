package com.khirye.rpc.nameservice;

import com.khirye.rpc.api.NameService;
import com.khirye.rpc.serialize.SerializeSupport;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class LocalFileNameService implements NameService {

    private static final Collection<String> schemes = Collections.singleton("file");
    private File file;

    @Override
    public void register(String serviceName, URI uri) throws IOException {
        log.info("Register service: {}, uri: {}", serviceName, uri);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = randomAccessFile.getChannel()) {
            FileLock fileLock = fileChannel.lock();
            try {
                int fileLength = (int) randomAccessFile.length();
                Metadata metadata;
                byte[] bytes;
                if (fileLength > 0) {
                    bytes = new byte[fileLength];
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    while (buffer.hasRemaining()) {
                        fileChannel.read(buffer);
                    }

                    metadata = SerializeSupport.parse(bytes);
                } else {
                    metadata = new Metadata();
                }
                List<URI> list = metadata.computeIfAbsent(serviceName, k -> new ArrayList<>());
                if (!list.contains(uri)) {
                    list.add(uri);
                }

                log.info(metadata.toString());

                bytes = SerializeSupport.serialize(metadata);
                fileChannel.truncate(bytes.length);
                fileChannel.position(0L);
                fileChannel.write(ByteBuffer.wrap(bytes));
                fileChannel.force(true);
            } finally {
                fileLock.release();
            }
        }
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        Metadata metadata;
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = randomAccessFile.getChannel()) {
            FileLock lock = fileChannel.lock();
            try {
                byte[] bytes = new byte[(int) randomAccessFile.length()];
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                while (byteBuffer.hasRemaining()) {
                    fileChannel.read(byteBuffer);
                }
                metadata = bytes.length == 0 ? new Metadata() : SerializeSupport.parse(bytes);
                log.info(metadata.toString());
            } finally {
                lock.release();
            }

        }

        List<URI> uris = metadata.get(serviceName);
        if (null == uris || uris.isEmpty()) {
            return null;
        } else {
            return uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
        }
    }

    @Override
    public void connect(URI serviceUri) {
        if (schemes.contains(serviceUri.getScheme())) {
            file = new File(serviceUri);
            return;
        }
        throw new RuntimeException("Unsupported scheme");
    }

    @Override
    public Collection<String> supportedSchemes() {
        return schemes;
    }
}
