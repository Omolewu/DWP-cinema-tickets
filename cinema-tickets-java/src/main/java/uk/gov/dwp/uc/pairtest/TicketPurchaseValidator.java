package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

final class TicketPurchaseValidator {

    private static final int MAX_TICKETS_PER_PURCHASE = 25;

    private TicketPurchaseValidator() {
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

        int totalTickets = 0;
        boolean hasAdultTicket = false;
        boolean hasChildOrInfantTicket = false;

        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request == null) {
                throw new InvalidPurchaseException("Ticket request cannot contain null values.");
            }

            TicketTypeRequest.Type ticketType = request.getTicketType();
            int quantity = request.getNoOfTickets();

            if (ticketType == null) {
                throw new InvalidPurchaseException("Ticket type cannot be null.");
            }

            if (quantity <= 0) {
                throw new InvalidPurchaseException("Number of tickets must be greater than zero.");
            }

            if (quantity > MAX_TICKETS_PER_PURCHASE - totalTickets) {
                throw new InvalidPurchaseException(
                        "Cannot purchase more than " + MAX_TICKETS_PER_PURCHASE
                                + " tickets in a single transaction.");
            }

            totalTickets += quantity;

            switch (ticketType) {
                case ADULT -> hasAdultTicket = true;
                case CHILD, INFANT -> hasChildOrInfantTicket = true;
            }
        }

        if (hasChildOrInfantTicket && !hasAdultTicket) {
            throw new InvalidPurchaseException("Child and infant tickets require at least one adult ticket.");
        }
    }

}
