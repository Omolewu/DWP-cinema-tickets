# Cinema Tickets - Java 21

A Java implementation of the DWP Cinema Tickets coding exercise. The solution is located in **`cinema-tickets-java/`** within this repository.

The service validates ticket purchase requests, calculates the total payment and determines the number of seats to reserve using the supplied payment and seat reservation services.

## Requirements

- Java 21 or later
- Apache Maven (3.9+ recommended)

## Building and Testing

From the repository root, navigate to the Java project:

```bash
cd cinema-tickets-java
```

Run the tests and build the project:

```bash
mvn clean verify
```

This compiles the Java application, runs the JUnit 5 tests and packages the project.

The tests use Mockito to isolate the external payment and seat reservation services.

## Business Rules

The implementation supports three ticket types:

| Ticket type | Price | Seat required |
|---|---:|---|
| Adult | £25 | Yes |
| Child | £15 | Yes |
| Infant | £0 | No |

A purchase must satisfy the following rules:

- The account ID must be greater than zero.
- At least one ticket must be requested.
- Every ticket request must contain a valid ticket type and a positive quantity.
- A maximum of 25 tickets may be purchased in one transaction, including infant tickets.
- Child and infant tickets require at least one adult ticket in the same purchase.
- Multiple requests for the same ticket type are accepted, with their quantities included in the combined totals.

Invalid purchases are rejected using `InvalidPurchaseException` before any payment or seat reservation is requested.

Validation follows a fail-fast approach. The account ID is checked first, followed by ticket request structure, individual quantities and the combined ticket limit. The adult accompaniment requirement is checked after the ticket requests have been examined.

## Implementation Approach

**TicketServiceImpl** coordinates the purchase process. It validates the request, calculates the payment amount and number of seats, then invokes the supplied `TicketPaymentService` and `SeatReservationService`.

Both services are provided through constructor injection. This keeps the implementation independent of their concrete implementations and allows external interactions to be verified in tests.

**TicketPurchaseValidator** handles purchase validation separately from payment and seat calculations, keeping the service focused on coordinating purchases and making the validation rules independently testable.

**TicketTypeRequest** is an immutable value object with final fields. Its existing constructor and getter methods are preserved.

Payment is requested before seat reservation. If payment throws an exception, seat reservation is not attempted.

## Testing Approach

The solution was developed incrementally using test-driven development (TDD), with tests used to establish expected behaviour before implementing or refining the corresponding functionality.

JUnit 5 and Mockito are used to verify:

- Account ID and ticket request validation.
- Ticket quantities and the 25-ticket boundary.
- Adult accompaniment requirements.
- Payment calculations across ticket types.
- Seat allocation, including infants requiring no seats.
- Multiple requests for the same ticket type.
- Payment and seat reservation ordering.
- Rejected purchases never call the external services.
- Constructor dependency validation and payment failure handling.

The test suite contains 33 test executions across `TicketPurchaseValidatorTest` and `TicketServiceImplTest`.

## Assumptions and Decisions

### Infant accompaniment

The requirements state that infants sit on an adult's lap and cannot be purchased without an adult ticket. However, they do not explicitly specify how many infants one adult may accompany.

The implementation requires at least one adult ticket but does not impose a one-infant-per-adult restriction.

An alternative interpretation would require one adult for each infant. This could be enforced through an isolated change to `TicketPurchaseValidator` if the business requirement were clarified.

### Ticket limit

Infant tickets count towards the maximum of 25 tickets because the limit applies to the number of tickets purchased, not the number of seats reserved.

### Invalid purchase requests

Zero and negative quantities, null or empty requests, null ticket types and invalid account IDs are rejected.

### Repeated ticket types

Separate requests for the same ticket type are accepted and included in the total payment and seat calculations.

### External service behaviour

The supplied payment and seat reservation services are treated as external dependencies. The exercise assumes that both operations succeed once requested.

No compensating transaction or payment rollback is implemented because reservation failure is outside the stated assumptions.

In a production system, a reservation failure after successful payment could leave a customer charged without receiving seats. Handling this would require an appropriate recovery or compensation mechanism.

### Monetary values

Although `BigDecimal` is generally preferred for decimal monetary calculations, this exercise uses `int` because ticket prices are whole-pound amounts and the supplied payment service accepts an integer. This avoids unnecessary complexity while preserving accuracy.

## Assessment Constraints

The implementation preserves the supplied `TicketService` interface and the code under the `thirdparty` packages. It uses the provided service contracts without modifying their behaviour.