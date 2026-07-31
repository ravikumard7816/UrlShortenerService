# UrlShortenerService
# URL Shortener Service (Agentic SDLC Prototype)

A production-grade, highly reliable URL Shortener Service developed using an **Agentic Software Engineering Execution Model** (OpenSpec methodology). This repository demonstrates end-to-end automated lifecycle orchestration—spanning requirement normalization, task decomposition, architectural design, implementation, and automated testing—under controlled human autonomy.

---

## 📌 Features

* **URL Shortening:** Generates unique, shortened aliases for long HTTP/HTTPS URLs.
* **Redirection:** Decodes short URLs and redirects requests to the original destination.
* **OpenSpec / Agentic Workflow:** Built using explicit dependency graphs, bounded retries, and policy guardrails across greenfield and brownfield scenarios.
* **Analytics & Reliability:** Built-in tracking readiness, defensive validation, and modular architecture.

---

## 🚀 Quick Start & API Usage

### Prerequisites
* Java 17 or higher
* Maven 3.8+

### Running the Application

```bash
# Clone the repository
git clone [https://github.com/ravikumard7816/UrlShortenerService.git](https://github.com/ravikumard7816/UrlShortenerService.git)
cd UrlShortenerService

# Build and run using Maven
mvn clean spring-boot:run

The service will start locally on http://localhost:8080.

📡 API Endpoints
1. Shorten a URL
Creates a short URL mapping for a given original URL.

HTTP Method: POST

URL: http://localhost:8080/shorten

Headers: Content-Type: application/json

Request Body:

JSON
[https://www.anewtesturl.com](https://www.anewtesturl.com)
Response (200 OK):

JSON
{
    "shortUrl": "http://localhost:8080/b"
}
🤖 Agentic Architecture & OpenSpec Execution
This service was engineered using an Agentic Orchestration Model that strictly follows an explicit dependency graph with entry/exit quality gates, human-in-the-loop governance checkpoints, and safe stateful retries.

Lifecycle Artifacts & Lineage
The lifecycle decision lineage and execution steps are documented in the following root specs:

PROPOSAL.md — Requirement normalization and scope boundaries.

SPEC.md — System specification, API contracts, and non-functional goals.

DESIGN.md — High-level architecture, module decomposition, and data flow.

TASKS.md — Task graph, execution sequencing, and dependencies.

EXPLORATION.md — Codebase analysis and dependency evaluations.

FINAL_SUMMARY.md — Engineering rationale, trade-offs, metrics, and risk control validation.

🧪 Testing & Validation
Run unit and integration test suites:

Bash
mvn test
Validation controls include:

URL format validation and sanitization.

Duplicate link key collision checks.

Controlled fallback behavior on invalid/expired keys.
