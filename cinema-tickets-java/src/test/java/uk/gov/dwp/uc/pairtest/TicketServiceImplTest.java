package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

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
        ticketService = new TicketServiceImpl(
                ticketPaymentService,
                seatReservationService);
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
                () -> ticketService.purchaseTickets(1L, childTicket));
    }

    @Test
    @DisplayName("Charges £25 and reserves one seat for a single adult ticket purchase")
    void shouldChargeAndReserveSeatForSingleAdultTicketPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        ticketService.purchaseTickets(1L, adultTicket);

        verify(ticketPaymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    @DisplayName("Charges £75 and reserves three seats for a purchase of three adult tickets")
    void shouldChargeAndReserveSeatsForMultipleAdultTicketPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        ticketService.purchaseTickets(1L, adultTicket);

        verify(ticketPaymentService).makePayment(1L, 75);
        verify(seatReservationService).reserveSeat(1L, 3);
    }

    @Test
    @DisplayName("Charges £55 and reserves three seats for one adult and two child tickets")
    void shouldChargeAndReserveSeatsForAdultAndChildTicketPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        ticketService.purchaseTickets(1L, adultTicket, childTicket);

        verify(ticketPaymentService).makePayment(1L, 55);
        verify(seatReservationService).reserveSeat(1L, 3);
    }

    @Test
    @DisplayName("Charges £25 and reserves one seat for one adult and two infant tickets")
    void shouldChargeAndReserveSeatsForAdultAndInfantTicketPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        ticketService.purchaseTickets(1L, adultTicket, infantTicket);

        verify(ticketPaymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    @DisplayName("Charges £95 and reserves five seats for a mixed ticket purchase")
    void shouldChargeAndReserveSeatsForMixedTicketPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket);

        verify(ticketPaymentService).makePayment(1L, 95);
        verify(seatReservationService).reserveSeat(1L, 5);
    }

    @Test
    @DisplayName("Process payment before reserving seats")
    void shouldProcessPaymentBeforeReservingSeats() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(1L, adultTicket, childTicket);

        InOrder inOrder = inOrder(ticketPaymentService, seatReservationService);
        inOrder.verify(ticketPaymentService).makePayment(1L, 40);
        inOrder.verify(seatReservationService).reserveSeat(1L, 2);
    }

    @Test
    @DisplayName("Does not process payment or reserve seats for invalid purchases")
    void shouldNotProcessPaymentOrReserveSeatsForInvalidPurchases() {
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, childTicket));

        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    @DisplayName("Does not reserve seats when payment fails")
    void shouldNotReserveSeatsWhenPaymentFails() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        RuntimeException paymentFailureException = new RuntimeException("Payment failed");
        doThrow(paymentFailureException)
                .when(ticketPaymentService)
                .makePayment(1L, 25);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> ticketService.purchaseTickets(1L, adultTicket));
        assertSame(paymentFailureException, thrown);

        verify(ticketPaymentService).makePayment(1L, 25);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("Aggregates multiple requests for the same ticket type")
    void shouldAggregateMultipleRequestsForSameTicketType() {
        TicketTypeRequest ticketRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest ticketRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);

        ticketService.purchaseTickets(1L, ticketRequest1, ticketRequest2);

        verify(ticketPaymentService).makePayment(1L, 125);
        verify(seatReservationService).reserveSeat(1L, 5);
    }

    @Test 
    @DisplayName ("Processes a purchase of exactly 25 tickets")
    void shouldProcessPurchaseOfExactly25Tickets() {
        TicketTypeRequest ticketRequest1 =
         new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10);
        TicketTypeRequest ticketRequest2 =
         new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 15);

        ticketService.purchaseTickets(1L, ticketRequest1, ticketRequest2);

        verify(ticketPaymentService).makePayment(1L, 475);
        verify(seatReservationService).reserveSeat(1L, 25);
    }

    @Test 
    @DisplayName ("Rejects a null payment service dependency")
    void shouldRejectNullPaymentServiceDependency() {
        assertThrows(
                NullPointerException.class,
                () -> new TicketServiceImpl(null, seatReservationService)
        );
    }

    @Test
    @DisplayName ("Rejects a null seat reservation service dependency")
    void shouldRejectNullSeatReservationServiceDependency() {
        assertThrows(
                NullPointerException.class,
                () -> new TicketServiceImpl(ticketPaymentService, null)
        );
    }
}