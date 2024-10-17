package hhplus.concertreservation.application.service;

import hhplus.concertreservation.domain.entity.Concert;
import hhplus.concertreservation.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public List<Concert> getConcerts(String concertName) {
        return concertRepository.findByConcertName(concertName);
    }
}
