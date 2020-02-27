run:
	./gradlew run

docker-run:
	docker run --rm -p 8080:8080 lukaswire/polls

docker-build:
	docker build -t lukaswire/polls:latest .

publish: docker-build
	docker push lukaswire/polls:latest
