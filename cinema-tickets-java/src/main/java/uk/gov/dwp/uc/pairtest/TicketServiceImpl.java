package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(
            TicketPaymentService ticketPaymentService,
            SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        TicketPurchaseValidator.validateAccountId(accountId);
        TicketPurchaseValidator.validateTicketRequests(ticketTypeRequests);

        int totalSeatsToReserve = 0;
        int totalAmountToPay = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            int quantity = request.getNoOfTickets();

            if (request.getTicketType() == TicketTypeRequest.Type.ADULT) {
                totalAmountToPay += quantity * 25;
                totalSeatsToReserve += quantity;
            } else if (request.getTicketType() == TicketTypeRequest.Type.CHILD) {
                totalAmountToPay += quantity * 15;
                totalSeatsToReserve += quantity;
            }
        }

        ticketPaymentService.makePayment(accountId, totalAmountToPay);
        seatReservationService.reserveSeat(accountId, totalSeatsToReserve);
    }

}
