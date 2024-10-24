package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.constant.PaymentStatus;
import hhplus.concertreservation.app.domain.entity.Payment;

public record PaymentResponse(
        Long reservationId,
        PaymentStatus paymentStatus
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getReservationId(),
                payment.getPaymentStatus()
        );
    }
}
