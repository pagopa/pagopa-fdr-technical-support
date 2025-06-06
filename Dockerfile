## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/ubi-quarkus-graalvmce-builder-image:22.3-java17@sha256:6e0894685358782ce5fee88537bcbb6c31256f1c7b4566182a56da934d03bb5f AS build
USER root
RUN microdnf install -y jq
COPY --chown=quarkus:quarkus mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/
COPY --chown=quarkus:quarkus generate_openapi_infra.sh /code/
USER quarkus
WORKDIR /code
RUN chmod +x ./mvnw && ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
COPY src /code/src
ARG QUARKUS_PROFILE
ARG APP_NAME

RUN ./mvnw package -DskipTests=true -Dquarkus.application.name=$APP_NAME -Dquarkus.profile=$QUARKUS_PROFILE

FROM registry.access.redhat.com/ubi8/openjdk-17:1.14@sha256:79585ca02551ecff9d368905d7ce387232b9fd328256e7a715ae3c4ec7b086d3

ENV LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=build /code/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /code/target/quarkus-app/*.jar /deployments/
COPY --from=build /code/target/quarkus-app/app/ /deployments/app/
COPY --from=build /code/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185

ARG QUARKUS_PROFILE
ARG APP_NAME

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Dquarkus.application.name=$APP_NAME -Dquarkus.profile=$QUARKUS_PROFILE -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"


