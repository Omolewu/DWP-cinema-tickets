package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
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

    static void validateTicketRequests(TicketTypeRequest... ticketTypeRequests) {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("Ticket request cannot be null or empty.");
        }

        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request == null) {
                throw new InvalidPurchaseException("Ticket request cannot contain null values.");
            }

            if (request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException("Number of tickets must be greater than zero.");
            }
        }
    }

}
