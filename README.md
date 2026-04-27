# GCP Java Automation Lab

![Java](https://img.shields.io/badge/Java-21-blue)
![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-green)
![GCP](https://img.shields.io/badge/GCP-Cloud--Storage-orange)
![Docker](https://img.shields.io/badge/Docker-Container-blue)
![GitHub Actions](https://img.shields.io/badge/GitHub--Actions-CI--CD-white?logo=github)
![Gradle](https://img.shields.io/badge/Gradle-8-brightgreen)
![Build Status](https://github.com/joanna-kaminska-qa/gcp-java-automation/actions/workflows/tests.yml/badge.svg)

An educational project demonstrating API test automation using **RestAssured**, **Docker**, and **Google Cloud Platform (GCP)** services.

## Project Objective
The main goal was to build a complete CI/CD pipeline that automatically verifies integration with **Google Cloud Storage** (bucket creation, file uploads) while adhering to cloud security best practices.

## Tech Stack
* **Language:** Java 21
* **API Testing:** RestAssured
* **Build Tool:** Gradle
* **Containerization:** Docker
* **Cloud:** Google Cloud Platform (Cloud Storage, Cloud Logging, IAM)
* **CI/CD:** GitHub Actions

## Architecture & Workflow
1.  **Local Development:** Tests developed in IntelliJ IDEA using RestAssured.
2.  **Containerization:** `Dockerfile` implementation to ensure tests can run in any environment.
3.  **GCP Infrastructure:** Service Account configuration with appropriate IAM roles (Cloud Storage Admin).
4.  **CI/CD Pipeline:** Automated test execution on GitHub Actions runners triggered by every `push` event.

## Security
The project is configured according to the **Zero Trust** principle:
* GCP Service Account keys (JSON) are excluded from the repository via `.gitignore`.
* For the CI/CD environment, keys are dynamically injected using **GitHub Secrets**.

## How to Run
To run tests locally, ensure you have a service account key file and set the environment variable:

```bash
export GCP_KEY_JSON=$(cat path/to/your-key.json)
./gradlew test
```

## Authors

Created by:

**Joanna Kamińska**  
GitHub: [https://github.com/joanna-kaminska-qa](https://github.com/joanna-kaminska-qa)

---

## License

This project is licensed under the **MIT License**.  
See the LICENSE file for details.