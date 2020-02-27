run:
	./gradlew run

docker-run:
	docker run --rm -p 8080:8080 poll-bot

docker-build:
	docker build -t poll-bot .
