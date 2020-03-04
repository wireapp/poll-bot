run:
	./gradlew run

integration-tests-pipeline:
	docker-compose stop; \
	echo "y" | docker-compose rm; \
	docker-compose up -d bot; \
	docker-compose up --abort-on-container-exit integration-tests; \
	RESULT=$$?; \
	docker-compose stop; \
	exit $$RESULT
