package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@DisplayName ("Ticket Purchase Validator")
public class TicketPurchaseValidatorTest {
    
    @Test 
    @DisplayName ("should reject account ID of zero")
    void shouldRejectAccountIdOfZero() {
       
        assertThrows(
            InvalidPurchaseException.class,
            () -> TicketPurchaseValidator.validateAccountId(0L)
        );
    }
}
