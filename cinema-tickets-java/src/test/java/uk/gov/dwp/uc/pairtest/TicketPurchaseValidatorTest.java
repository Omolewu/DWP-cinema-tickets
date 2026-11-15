package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketPurchaseValidatorTest {
    
    @Test 
    void shouldRejectAccountIdOfZero() {
       
        assertThrows(
            InvalidPurchaseException.class,
            () -> TicketPurchaseValidator.validateAccountId(0L)
        );
    }
}
