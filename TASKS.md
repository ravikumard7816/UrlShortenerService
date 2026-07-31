# Phase 5: Tasks

This document breaks down the implementation of the proposed features into a checklist of actionable development tasks.

## Section 1: Initial Implementation (Completed)
- [x] **Task 1.1:** Add `spring-retry` to `pom.xml`.
- [x] **Task 1.2:** Add Cucumber and WireMock dependencies to `pom.xml`.
- [x] **Task 2.1:** Annotate `UrlShortenerServiceApplication` with `@EnableRetry`.
- [x] **Task 2.2:** Create `ServiceUnavailableException.java`.
- [x] **Task 2.3 & 2.4:** Add `@Retryable` and `@Recover` to `UrlShortenerService`.
- [x] **Task 2.5:** Create unit test for retry logic.
- [x] **Task 3.1 - 3.5:** Implement external URL validation feature.
- [x] **Task 4.1 - 4.8:** Implement ATDD tests with Cucumber and file-based WireMock stubs.

## Section 2: Refine Retry Logic (New)

- [ ] **Task 5.1:** Update `@Retryable` annotations in `UrlShortenerService` to `exclude` non-retryable exceptions like `ShortUrlNotFoundException`.
- [ ] **Task 5.2:** Add a new unit test to `UrlShortenerServiceTest` to verify that a `ShortUrlNotFoundException` is **not** retried (i.e., the repository method is called only once).
- [ ] **Task 5.3:** Add the new "Requesting a non-existent short URL" scenario to `UrlShortener.feature`.
- [ ] **Task 5.4:** Implement the new "Given" step (`the short URL {string} does not exist`) in `UrlShortenerStepDefinitions`.
- [ ] **Task 5.5:** Implement the new "Then" step (`the system has attempted the lookup only 1 time`) in `UrlShortenerStepDefinitions`.
- [ ] **Task 5.6:** Update all relevant `.md` files to reflect the new logic (already completed in this pass).
