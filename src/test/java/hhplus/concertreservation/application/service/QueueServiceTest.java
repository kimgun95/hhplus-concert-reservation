package hhplus.concertreservation.application.service;

import hhplus.concertreservation.app.application.service.QueueService;
import hhplus.concertreservation.app.domain.constant.QueueStatus;
import hhplus.concertreservation.app.domain.dto.QueryQueueDto;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.app.domain.repository.QueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueueServiceTest {

    @Mock
    private QueueRepository queueRepository;

    @InjectMocks
    private QueueService sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 대기열발급시이미발급한대기열토큰이있다면_기존대기열토큰가져오기() {
        Long userId = 1L;
        Queue existingQueue = Queue.create(userId);
        when(queueRepository.findByUserId(userId)).thenReturn(Optional.of(existingQueue));

        Queue result = sut.getQueue(userId);

        assertEquals(existingQueue, result);
        verify(queueRepository, never()).save(any(Queue.class));
    }

    @Test
    void 대기열최초발급이라면_대기열토큰발급하기() {
        Long userId = 1L;
        when(queueRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        Queue result = sut.getQueue(userId);

        assertEquals(userId, result.getUserId());
        verify(queueRepository).save(any(Queue.class));
    }

    @Test
    void 대기열상태가READY이면서클라이언트보다앞순번인대기열갯수() {
        String token = UUID.randomUUID().toString();
        Queue queue = Queue.create(1L);
        when(queueRepository.findByToken(token)).thenReturn(Optional.of(queue));
        when(queueRepository.countByUserQueueStatusAndUserIdLessThan(QueueStatus.READY, queue.getId())).thenReturn(5L);

        QueryQueueDto result = sut.queryQueue(token);

        assertNotNull(result);
        assertEquals(queue, result.queue());
        assertEquals(5L, result.queueCount());
    }

    @Test
    void 클라이언트가제공한토큰값을통해대기열토큰조회하기_성공() {
        String token = UUID.randomUUID().toString();
        Queue queue = Queue.create(1L);
        Long count = 1L;
        when(queueRepository.findByToken(token)).thenReturn(Optional.of(queue));
        when(queueRepository.countByUserQueueStatusAndUserIdLessThan(QueueStatus.READY, queue.getId())).thenReturn(count);

        QueryQueueDto result = sut.queryQueue(token);

        assertEquals(queue, result.queue());
        assertEquals(count, result.queueCount());
    }

    @Test
    void 클라이언트가제공한토큰값을통해대기열토큰조회하기_실패() {
        String token = UUID.randomUUID().toString();
        when(queueRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            sut.queryQueue(token);
        });
    }

    @Test
    void 스케줄러는대기열상태가ACTIVE인토큰을카운트_ACTIVE토큰이최대활성화갯수보다작다면_READY토큰을가져와ACTIVE로전환한다() {
        when(queueRepository.countByUserQueueStatus(QueueStatus.ACTIVE)).thenReturn(3L);
        Queue queue1 = Queue.create(1L);
        Queue queue2 = Queue.create(2L);
        when(queueRepository.findTopByUserQueueStatusOrderByUserIdAsc(
                QueueStatus.READY, PageRequest.of(0, 2)))
                .thenReturn(Arrays.asList(queue1, queue2));

        sut.checkActiveQueues();

        verify(queueRepository).saveAll(Arrays.asList(queue1, queue2));
        assertEquals(QueueStatus.ACTIVE, queue1.getUserQueueStatus());
        assertEquals(QueueStatus.ACTIVE, queue2.getUserQueueStatus());
    }

    @Test
    void 스케줄러는만료된대기열토큰을탐색하여삭제한다() {
        LocalDateTime now = LocalDateTime.now();

        sut.deleteExpiredQueue();

        verify(queueRepository).deleteByExpiredAtBefore(any(LocalDateTime.class));
    }
}