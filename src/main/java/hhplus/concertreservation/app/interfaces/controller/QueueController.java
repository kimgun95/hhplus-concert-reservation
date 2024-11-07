package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.QueueService;
import hhplus.concertreservation.app.domain.dto.QueueRank;
import hhplus.concertreservation.app.interfaces.response.IssueQueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/queue/issue")
    public ResponseEntity<IssueQueueResponse> issueQueueToken(
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(IssueQueueResponse.from(queueService.issueToken(userId)));
    }

    @GetMapping("/queue/query")
    public ResponseEntity<QueueRank> queryQueueToken(
            @RequestHeader("AuthorizationQueue") String token
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(queueService.queryToken(token));
    }

}
