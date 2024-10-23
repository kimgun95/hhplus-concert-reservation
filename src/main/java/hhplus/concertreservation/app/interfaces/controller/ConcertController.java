package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.ConcertService;
import hhplus.concertreservation.app.interfaces.response.ConcertDateTimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/concert")
    public ResponseEntity<List<ConcertDateTimeResponse>> concertDateTime(
            @RequestHeader String token,
            @RequestParam String concertName
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(concertService.getConcerts(concertName)
                        .stream().map(ConcertDateTimeResponse::from)
                        .toList());
    }
}
