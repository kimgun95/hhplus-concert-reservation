package hhplus.concertreservation.app.interfaces.request;

public record ChargePointRequest(
        Long userId,
        Long amount
) {
}
