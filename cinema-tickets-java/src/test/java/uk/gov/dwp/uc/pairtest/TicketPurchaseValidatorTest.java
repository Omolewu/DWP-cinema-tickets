package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    @DisplayName ("Ticket request validation")
    class TicketRequestValidationTests {

        @Test 
        @DisplayName ("Rejects a purchase when ticket request is null")
        void shouldRejectNullTicketRequest() {
            assertThrows(
                    InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validateTicketRequest(null)
            );
        }
    }
}
