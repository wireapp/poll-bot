FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine AS build
LABEL description="Wire Poll Bot"
LABEL project="wire-bots:polls"

ENV PROJECT_ROOT /src
WORKDIR $PROJECT_ROOT

COPY . $PROJECT_ROOT

RUN chmod +x gradlew

RUN ./gradlew distTar --no-daemon

# Runtime
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV APP_ROOT /app
WORKDIR $APP_ROOT

COPY --from=build /src/build/distributions/polls-*.tar $APP_ROOT/

RUN mkdir $APP_ROOT/run
RUN tar -xvf polls-*.tar --strip-components=1 -C $APP_ROOT/run

EXPOSE 8080

CMD $APP_ROOT/run/bin/polls
