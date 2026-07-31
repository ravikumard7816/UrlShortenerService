# Phase 4: Specification

This document provides the formal specification for the new features in the Gherkin language. These acceptance criteria will be automated to validate the application's behavior from an end-user perspective.

## `UrlShortener.feature`

```gherkin
Feature: URL Shortener Service - Core Functionality and External Validation

  Background:
    Given the application is running
    And the external URL safety service is running

  @happy-path
  Scenario: Shortening a valid and safe URL
    # Tests the primary success path.
    Given the safety service will report "https://www.google.com" as safe
    When a user sends a POST request to "/shorten" with the body "https://www.google.com"
    Then the user receives a 200 OK response
    And the response contains a short URL

  @happy-path
  Scenario: Redirecting a valid short URL
    # Ensures the redirection logic works as expected.
    Given the safety service will report "https://www.yahoo.com" as safe
    And a user has already shortened "https://www.yahoo.com" to a short URL
    When the user sends a GET request to the short URL path
    Then the user is redirected to "https://www.yahoo.com" with a 302 Found status

  @failure-case
  Scenario: Attempting to shorten an unsafe URL
    # Tests handling of a failure case from the external validation service.
    Given the safety service will report "https://www.malicious-site.com" as unsafe
    When a user sends a POST request to "/shorten" with the body "https://www.malicious-site.com"
    Then the user receives a 400 Bad Request error
    And the error message indicates the URL is unsafe

  @resilience
  Scenario: Service is unavailable due to database issues
    # Tests the retry logic for transient, retryable errors.
    Given the database is down
    When a user sends a POST request to "/shorten" with the body "https://www.resilience-test.com"
    Then the user receives a 503 Service Unavailable error
    And the system has attempted the operation 3 times

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
