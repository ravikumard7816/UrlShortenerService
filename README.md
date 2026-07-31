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
