package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

final class TicketPurchaseValidator {
    
    private TicketPurchaseValidator() {
        // Private constructor to prevent instantiation
    }

    static void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be greater than zero.");
        }
    }
    
}
