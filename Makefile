run:
	./gradlew run

db:
	docker-compose up -d db

compose:
	docker-compose up -d db && docker-compose up bot

docker-run:
	docker run --rm -p 8080:8080 lukaswire/polls

docker-build:
	docker build -t lukaswire/polls:latest .

publish: docker-build
	docker push lukaswire/polls:latest

kube-deploy:
	kubectl delete pod -l name=poll -n staging

kube-logs:
	kubectl logs --follow -l name=poll -n staging

kube-describe:
	kubectl describe  pods -l name=poll -n staging
