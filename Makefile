.DEFAULT_GOAL := help

help:
	@echo ''
	@echo 'Makefile for '
	@echo '     make help           show this information'
	@echo '     make clean          shutdown running container and delete image'
	@echo '     make build          build a dockerfile for testing the jenkins configuration'
	@echo '     make run            run the dockerfile in the background'
	@echo '     make healthcheck    run healthcheck script to test if Jenkins has successfully booted'

clean:
	docker kill jenkins-1.6
	docker rm jenkins-1.6
	docker rmi jenkins-1.6

build:
	docker build -t jenkins-1.6 .

run:
	docker run --name jenkins-1.6 -p 8080:8080 -d jenkins-1.6

healthcheck:
	./healthcheck.sh
