package hhplus.concertreservation.app.infrastructure;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockExecutor {

    private final RedissonClient redissonClient;

    public <T> T execute(String lockName, Callable<T> callable) throws Exception {
        RLock lock = redissonClient.getLock(lockName);

        try {
            // 최대 5초 대기, 3초 임대
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new IllegalStateException();
            }

            return callable.call();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
