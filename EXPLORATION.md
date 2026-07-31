# Phase 1: Exploration

## 1. Current State Analysis

The URL Shortener service is a functional Spring Boot application with the following characteristics:

*   **Core Functionality:** It can create short URLs from long URLs and redirect users.
*   **Analytics:** It tracks the number of times each short URL is accessed.
*   **Basic Retry Logic:** A basic retry mechanism using Spring Retry is in place, which re-attempts operations on any `DataAccessException`.
*   **Tech Stack:** Java 21, Spring Boot 3, Spring Data JPA, H2, Maven.
*   **Testing:** The service has unit tests and a suite of ATDD tests using Cucumber and WireMock.

## 2. Goals for This Phase

### a. Implement Retry Logic (Existing)
The service has a basic mechanism to handle transient failures.

### b. Enhance Unit Test Coverage (Existing)
The test suite covers happy paths and some failure conditions.

### c. Introduce ATDD (Existing)
ATDD is implemented using Cucumber, validating end-to-end flows.

### d. Integrate WireMock (Existing)
WireMock is used to simulate an external URL validation service.

### e. **(New Goal) Refine Retry Logic to Distinguish Retryable vs. Non-Retryable Exceptions**
The current implementation is too broad. It retries on any database exception. However, some exceptions are "permanent" and should not be retried. For example, trying to access a short URL that doesn't exist (`ShortUrlNotFoundException`) will always fail, no matter how many times we retry.

**Our goal is to make the retry logic smarter:**
*   **Retryable errors:** Continue to retry on transient infrastructure issues (e.g., database connection lost).
*   **Non-Retryable errors:** Immediately fail on permanent application-level errors (e.g., data not found). This prevents wasteful retries and provides a faster response to the user for failures that are guaranteed to persist.
