package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.QueueService;
import hhplus.concertreservation.app.interfaces.response.IssueQueueResponse;
import hhplus.concertreservation.app.interfaces.response.QueryQueueResponse;
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
            @RequestParam Long userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(IssueQueueResponse.from(queueService.getQueue(userId)));
    }

    @GetMapping("/queue/query")
    public ResponseEntity<QueryQueueResponse> queryQueueToken(
            @RequestHeader String token
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(QueryQueueResponse.from(queueService.queryQueue(token)));
    }

}
