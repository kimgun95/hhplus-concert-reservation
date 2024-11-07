package hhplus.concertreservation.app.domain.dto;

import hhplus.concertreservation.app.domain.constant.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueRank {
    private Long rank;
    private QueueStatus status;
}
