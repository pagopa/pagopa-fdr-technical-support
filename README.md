# FdR Technical Support API

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-fdr-technical-support&metric=alert_status)](https://sonarcloud.io/dashboard?id=pagopa_pagopa-fdr-technical-support)


---
## API Documentation 📖
See the [OpenAPI 3 here.](https://raw.githubusercontent.com/pagopa/pagopa-fdr-technical-support/openapi/openapi.json)

---

## Technology Stack
- Maven
- Java 17
- Quarkus

## Running the infrastructure 🚀

### Prerequisites
- git
- maven
- jdk

### Setup
With skdman,in terminal:

`sdk install java 17.0.7-graal`

`sdk use java 17.0.7-graal`

or download **java 17.0.7-graal** and set as java home


### Run docker container
The docker compose runs:
- ELK
    - elasticsearch
    - logstash
    - [kibana](http://localhost:5601/)
- Monitoring
    - alertmanager
    - [prometheus](http://localhost:9090/),
    - [grafana](http://localhost:3000/) (user: ```admin```, password: ```admin```)
- Tracing
    - otel-collector
    - [jaeger](http://localhost:16686/)


To run locally, from the main directory, execute
`sh run-local-infra.sh <project-name>`

From `./docker` directory
`sh ./run_docker.sh local|dev|uat|prod`

ℹ️ Note: for PagoPa ACR is required the login `az acr login -n <acr-name>`

---

## Develop Locally 💻

### Prerequisites
- git
- maven
- jdk-11

### Run the project

Run in development mode with command
`quarkus dev`

### Quarkus Profiles

`dev` active in development

`test` active in tests

`openapi` active only for openapi generation

`prod` default for run

### Testing 🧪

#### Unit testing

To run the **Junit** tests:

`mvn clean verify`


---

## Contributors 👥
Made with ❤️ by PagoPa S.p.A.

### Mainteiners
See `CODEOWNERS` file
