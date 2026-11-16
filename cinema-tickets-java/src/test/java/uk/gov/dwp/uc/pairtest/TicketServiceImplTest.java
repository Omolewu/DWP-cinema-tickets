package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@DisplayName("Ticket purchase service")
class TicketServiceImplTest {

    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;
    private TicketServiceImpl ticketService;
    
    @BeforeEach
    void setup() {
        ticketPaymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        ticketService =
         new TicketServiceImpl(
            ticketPaymentService, 
            seatReservationService
        );    
    }
    @Test
    @DisplayName("should reject purchase when account ID is zero")
    void shouldRejectPurchaseWhenAccountIdIsZero() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(0L, ticketTypeRequest));
    }

    @Test
    @DisplayName("Rejects child ticket purchase without an adult ticket")
    void shouldRejectChildTicketPurchaseWithoutAdultTicket() {
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, childTicket)
            );
    }

    @Test 
    @DisplayName ("Charges £25 and reserves one seat for a single adult ticket purchase")
    void shouldChargeAndReserveSeatForSingleAdultTicketPurchase() {
        TicketTypeRequest adultTicket =
         new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        ticketService.purchaseTickets(1L, adultTicket);

       verify(ticketPaymentService).makePayment(1L, 25);
       verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    @DisplayName ("Charges £75 and reserves three seats for a purchase of three adult tickets")
    void shouldChargeAndReserveSeatsForMultipleAdultTicketPurchase() {
        TicketTypeRequest adultTicket =
         new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        ticketService.purchaseTickets(1L, adultTicket);

       verify(ticketPaymentService).makePayment(1L, 75);
       verify(seatReservationService).reserveSeat(1L, 3);
    }

    @Test 
    @DisplayName ("Charges £55 and reserves three seats for one adult and two child tickets")
    void shouldChargeAndReserveSeatsForAdultAndChildTicketPurchase() {
        TicketTypeRequest adultTicket =
         new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest childTicket =
         new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        ticketService.purchaseTickets(1L, adultTicket, childTicket);

       verify(ticketPaymentService).makePayment(1L, 55);
       verify(seatReservationService).reserveSeat(1L, 3);
    }

    @Test 
    @DisplayName ("Charges £25 and reserves one seat for one adult and two infant tickets")
    void shouldChargeAndReserveSeatsForAdultAndInfantTicketPurchase() {
        TicketTypeRequest adultTicket =
         new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantTicket =
         new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        ticketService.purchaseTickets(1L, adultTicket, infantTicket);

       verify(ticketPaymentService).makePayment(1L, 25);
       verify(seatReservationService).reserveSeat(1L, 1);
    }
}