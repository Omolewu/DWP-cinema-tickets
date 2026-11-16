package uk.gov.dwp.uc.pairtest;

import java.util.Objects;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    private static final int ADULT_TICKET_PRICE = 25;
    private static final int CHILD_TICKET_PRICE = 15;
    private static final int INFANT_TICKET_PRICE = 0;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(
            TicketPaymentService ticketPaymentService,
            SeatReservationService seatReservationService) {
        this.ticketPaymentService = Objects.requireNonNull(
                ticketPaymentService, "Ticket payment service cannot be null.");
        this.seatReservationService = Objects.requireNonNull(
                seatReservationService, "Seat reservation service cannot be null.");
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
            TicketTypeRequest.Type ticketType = request.getTicketType();

            int ticketPrice = switch (ticketType) {
                case ADULT -> ADULT_TICKET_PRICE;
                case CHILD -> CHILD_TICKET_PRICE;
                case INFANT -> INFANT_TICKET_PRICE;
            };

            totalAmountToPay += quantity * ticketPrice;

            if (ticketType != TicketTypeRequest.Type.INFANT) {
                totalSeatsToReserve += quantity;
            }
        }

        ticketPaymentService.makePayment(accountId, totalAmountToPay);
        seatReservationService.reserveSeat(accountId, totalSeatsToReserve);
    }

}
