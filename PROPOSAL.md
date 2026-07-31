# Phase 2: Proposal

Based on the goals identified, this document proposes the following technical solutions and strategies.

## 1. Retry Logic Implementation

*   **Tool:** Spring Retry (already in use).
*   **Strategy:**
    1.  **Refine `@Retryable` Configuration:** We will update the `@Retryable` annotation on our service methods. Instead of just including retryable exceptions, we will now also explicitly **exclude** non-retryable ones.
    2.  **Retryable Exceptions:** We will continue to retry on `DataAccessException`, which represents transient database issues.
    3.  **Non-Retryable Exceptions:** We will configure the annotation to **not** retry on our custom application exceptions, such as `ShortUrlNotFoundException` and `InvalidUrlException`. This ensures that if a user provides bad data or requests a non-existent URL, the system fails fast without pointless retries.
    4.  The updated annotation will look like this: `@Retryable(value = { DataAccessException.class }, exclude = { ShortUrlNotFoundException.class, InvalidUrlException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))`.

## 2. Enhanced Unit Testing

*   **Tools:** JUnit 5, Mockito.
*   **Strategy:**
    1.  **New Unit Test for Non-Retryable Failure:** We will add a new test to `UrlShortenerServiceTest` to verify the new "fail-fast" behavior. This test will mock the repository to throw a `ShortUrlNotFoundException` and will verify that the service method is called **only once** (i.e., it does not retry).

## 3. ATDD Implementation

*   **Tools:** Cucumber, Spring Boot Test, WireMock.
*   **Strategy:**
    1.  **New ATDD Scenario:** We will add a new scenario to our `UrlShortener.feature` file to describe and validate this behavior from an end-user's perspective. The scenario will test that when a user requests a non-existent short URL, the system fails immediately with a 404 error, and no retries are attempted.

## 4. WireMock Integration for Service Virtualization

*   **Tool:** WireMock (already in use).
*   **Strategy:** No changes are needed for WireMock in this phase, as we are focusing on internal application logic.
