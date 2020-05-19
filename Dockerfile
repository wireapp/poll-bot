FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine AS build
LABEL description="Wire Poll Bot"
LABEL project="wire-bots:polls"

ENV PROJECT_ROOT /src
WORKDIR $PROJECT_ROOT

# download wait-for script
RUN wget https://raw.githubusercontent.com/LukasForst/wait-for/master/wait-for
RUN chmod +x wait-for

# ------------------ App specific ------------------
# Copy gradle settings
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew $PROJECT_ROOT/
# Make sure gradlew is executable
RUN chmod +x gradlew
# Copy gradle specification
COPY gradle $PROJECT_ROOT/gradle
# Download gradle
RUN ./gradlew --version
# download and cache dependencies
RUN ./gradlew resolveDependencies --no-daemon

# Copy project and build
COPY src $PROJECT_ROOT
RUN ./gradlew distTar --no-daemon

# Runtime
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV APP_ROOT /app
WORKDIR $APP_ROOT

# Obtain built from the base
COPY --from=build /src/build/distributions/app.tar $APP_ROOT/

# Extract executables
RUN mkdir $APP_ROOT/run
RUN tar -xvf app.tar --strip-components=1 -C $APP_ROOT/run

# ------------------ Wire common ------------------
# copy wait for script for running in kubernetes
COPY --from=build /src/wait-for /wait-for
# create version file
ARG release_version=development
ENV RELEASE_FILE_PATH=$APP_ROOT/run/release.txt
RUN echo $release_version > $RELEASE_FILE_PATH
# enable json logging
ENV JSON_LOGGING=true
# /------------------ Wire common -----------------

EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "/app/run/bin/polls"]
