package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketServiceImplTest {

   private final TicketServiceImpl ticketService = new TicketServiceImpl();

    @Test
    void shouldRejectPurchaseWhenAccountIdIsZero() {
        TicketTypeRequest ticketTypeRequest = 
        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(0L, ticketTypeRequest)
        );
    }
}