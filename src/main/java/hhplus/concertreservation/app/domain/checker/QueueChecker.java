package hhplus.concertreservation.app.domain.checker;

import hhplus.concertreservation.app.domain.constant.QueueStatus;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class QueueChecker {
    public Queue getOrThrowIfNotFound(Optional<Queue> optionalQueue) {
        return optionalQueue.orElseThrow(() -> new FailException(ErrorCode.EXPIRED_QUEUE_TOKEN));
    }

    public void checkActiveOrThrow(Queue queue) {
        if (queue.getUserQueueStatus() != QueueStatus.ACTIVE)
            throw new FailException(ErrorCode.INVALID_QUEUE_STATUS);
    }
}
