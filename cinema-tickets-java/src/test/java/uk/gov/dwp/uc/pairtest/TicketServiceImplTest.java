package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;

@DisplayName ("Ticket purchase service")
class TicketServiceImplTest {

   private final TicketServiceImpl ticketService = new TicketServiceImpl();

    @Test
    @DisplayName ("should reject purchase when account ID is zero")
    void shouldRejectPurchaseWhenAccountIdIsZero() {
        TicketTypeRequest ticketTypeRequest = 
        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(0L, ticketTypeRequest)
        );
    }
}