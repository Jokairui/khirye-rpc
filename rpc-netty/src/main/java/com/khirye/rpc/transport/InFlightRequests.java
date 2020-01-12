package com.khirye.rpc.transport;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class InFlightRequests implements Closeable {

    private final static long TIMEOUT_SEC = 10L;
    private final Semaphore semaphore = new Semaphore(10);
    private final Map<Integer, ResponseFuture> futureMap = new HashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    public InFlightRequests() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, TIMEOUT_SEC, TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    private void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> {
            if(System.nanoTime() - entry.getValue().getTimestamp() > TimeUnit.SECONDS.convert(TIMEOUT_SEC, TimeUnit.NANOSECONDS)) {
                log.info("removed the timeout future, key is {}", entry.getKey());
                semaphore.release();
                return true;
            }
            return false;
        });
    }

    public void put(ResponseFuture responseFuture) throws InterruptedException, TimeoutException {
        if (semaphore.tryAcquire(TIMEOUT_SEC, TimeUnit.SECONDS)) {
            futureMap.put(responseFuture.getRequestId(), responseFuture);
            return;
        }
        throw new TimeoutException();
    }

    public ResponseFuture remove(int requestId) {
        ResponseFuture future = futureMap.remove(requestId);

        //可能恰好被超时检查的定时任务给remove掉了
        if (null != future) {
            semaphore.release();
        }
        return future;
    }

    @Override
    public void close() throws IOException {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
