# Phase 3: Design

This document outlines the design for the new features, detailing changes to the application's architecture, classes, and testing structure.

## 1. Retry Logic Design

*   **Configuration:**
    *   `UrlShortenerServiceApplication` remains annotated with `@EnableRetry`.
*   **Service Layer (`UrlShortenerService.java`):**
    *   The `@Retryable` annotations on the service methods will be updated to both include retryable exceptions and exclude non-retryable ones.
    *   The annotation will be configured as follows: `@Retryable(value = { DataAccessException.class }, exclude = { ShortUrlNotFoundException.class, InvalidUrlException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))`. This ensures that transient database errors are retried, but application-level errors like "not found" or "invalid URL" fail immediately.
    *   The `@Recover` method remains unchanged, as it will only be triggered if the `DataAccessException` retries are exhausted.

## 2. ATDD and WireMock Design

### a. Project Structure Changes
*   No new files or directories are needed. We will modify existing files.

### b. Cucumber Feature File (`UrlShortener.feature`)

A new scenario will be added to the feature file to test the non-retryable logic.

```gherkin
  @fast-fail
  Scenario: Requesting a non-existent short URL
    # This scenario tests that the system fails fast for non-retryable errors.
    # When a short URL does not exist, the service should not waste time retrying.
    # It should immediately return a 404 Not Found error.
    Given the database is running
    And the short URL "non-existent-url" does not exist
    When a user sends a GET request to "/non-existent-url"
    Then the user receives a 404 Not Found error
    And the system has attempted the lookup only 1 time
```

### c. Step Definitions and Test Configuration

*   **`UrlShortenerStepDefinitions.java`:**
    *   A new "Given" step will be added: `the short URL {string} does not exist`. This step will configure the `UrlMappingRepository` mock to return an empty `Optional` when that specific short URL is requested.
    *   A new "Then" step will be added: `the system has attempted the lookup only 1 time`. This step will use Mockito's `verify` to confirm that the repository's `findByShortUrl` method was called exactly once.

### d. Application Code Changes
*   The primary change will be updating the `@Retryable` annotations in `UrlShortenerService.java` as described above.
