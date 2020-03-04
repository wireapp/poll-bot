FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine AS build
LABEL description="Integration tests for Wire Poll Bot"
LABEL project="wire-bots:polls-integration-tests"

ENV PROJECT_ROOT /src
WORKDIR $PROJECT_ROOT

# Copy gradle settings
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew $PROJECT_ROOT/
# Make sure gradlew is executable
RUN chmod +x gradlew
# Copy gradle specification
COPY gradle $PROJECT_ROOT/gradle
# Download gradle
RUN ./gradlew --version

# Copy project and build
COPY . $PROJECT_ROOT
RUN ./gradlew assemble --info --no-daemon

# Execute tests
EXPOSE 8081
CMD ./gradlew test --no-daemon --info
