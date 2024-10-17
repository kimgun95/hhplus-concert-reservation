package hhplus.concertreservation.application.service;

import hhplus.concertreservation.domain.entity.Concert;
import hhplus.concertreservation.domain.repository.ConcertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertService sut;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 콘서트이름으로콘서트시간조회하기_성공() {
        // given
        String concertName = "LOA ON";
        Concert concert1 = Concert.builder()
                .id(1L)
                .concertName(concertName)
                .concertDate(LocalDateTime.now().plusDays(5))
                .build();
        Concert concert2 = Concert.builder()
                .id(2L)
                .concertName(concertName)
                .concertDate(LocalDateTime.now().plusDays(10))
                .build();
        List<Concert> concertList = Arrays.asList(concert1, concert2);

        when(concertRepository.findByConcertName(concertName)).thenReturn(concertList);

        // when
        List<Concert> result = sut.getConcerts(concertName);

        // then
        assertEquals(2, result.size());
    }

}