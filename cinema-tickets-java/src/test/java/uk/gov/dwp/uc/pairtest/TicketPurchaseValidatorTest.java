package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@DisplayName("Ticket purchase validator")
class TicketPurchaseValidatorTest {

    @Nested
    @DisplayName("Account ID validation")
    class AccountIdValidationTests {
        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L })
        @DisplayName("Rejects zero and negative account IDs")
        void shouldRejectZeroOrNegativeAccountId(long accountId) {

            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateAccountId(accountId));
        }

        @Test
        @DisplayName("Rejects a null account ID")
        void shouldRejectNullAccountId() {

            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateAccountId(null));
        }

        @Test
        @DisplayName("Accepts a valid account ID")
        void shouldAcceptValidAccountId() {

            assertDoesNotThrow(
                    () -> TicketPurchaseValidator.validateAccountId(1L));

        }
    }

    @Nested
    @DisplayName("Ticket request validation")
    class TicketRequestValidationTests {

        @Test
        @DisplayName("Rejects a purchase when ticket request is null")
        void shouldRejectNullTicketRequest() {
            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateTicketRequests((TicketTypeRequest[]) null));
        }

        @Test
        @DisplayName("Rejects an empty ticket request array")
        void shouldRejectEmptyTicketRequestArray() {
            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateTicketRequests(new TicketTypeRequest[0]));
        }

        @Test
        @DisplayName("Rejects a null element in the ticket request array")
        void shouldRejectNullElementInTicketRequestArray() {
            TicketTypeRequest[] ticketRequests = new TicketTypeRequest[] { null };

            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateTicketRequests(ticketRequests));
        }
    }

    @Nested
    @DisplayName("Ticket quantity validation")
    class TicketQuantityValidationTests {

        @ParameterizedTest
        @ValueSource(ints = { 0, -1 })
        @DisplayName("Rejects zero and negative ticket quantities")
        void shouldRejectZeroOrNegativeTicketQuantities(int quantity) {
            TicketTypeRequest ticketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, quantity);

            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateTicketRequests(ticketRequest));
        }

        @Test
        @DisplayName("Rejects purchase exceeding maximum ticket limit of 25")
        void shouldRejectPurchaseExceedingMaximumTicketLimit() {
            TicketTypeRequest ticketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateTicketRequests(ticketRequest));
        }

        @Test
        @DisplayName("Rejects combined ticket purchase exceeding maximum ticket limit of 25")
        void shouldRejectCombinedTicketPurchaseExceedingMaximumTicketLimit() {
            TicketTypeRequest ticketRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15);
            TicketTypeRequest ticketRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 11);

            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateTicketRequests(ticketRequest1, ticketRequest2));
        }

    }

}
