package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@DisplayName ("Ticket purchase validator")
public class TicketPurchaseValidatorTest {
    
    @Test 
    @DisplayName ("should reject account ID of zero")
    void shouldRejectAccountIdOfZero() {
       
        assertThrows(
            InvalidPurchaseException.class,
            () -> TicketPurchaseValidator.validateAccountId(0L)
        );
    }

    @Test
    @DisplayName ("should reject account ID of negative value")
    void shouldRejectAccountIdOfNegativeValue() {
       
        assertThrows(
            InvalidPurchaseException.class,
            () -> TicketPurchaseValidator.validateAccountId(-1L)
        );
    }

    @Test
    @DisplayName ("should reject null account ID")
    void shouldRejectNullAccountId() {
       
        assertThrows(
            InvalidPurchaseException.class,
            () -> TicketPurchaseValidator.validateAccountId(null)
        );
    }
}
