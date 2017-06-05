SHELL := /usr/bin/env bash
.DEFAULT_GOAL := help
.PHONY: clean build

help:
	@echo ''
	@echo 'Makefile for '
	@echo '     make help           show this information'
	@echo '     make clean          shutdown running container and delete image'
	@echo '     make build          build a dockerfile for testing the jenkins configuration'
	@echo '     make run            run the dockerfile in the background'
	@echo '     make healthcheck    run healthcheck script to test if Jenkins has successfully booted'
	@echo '     make quality        run codenarc on groovy source and tests'

clean:
# run the following docker commands with '|| true' because they do not have a 'quiet' flag
	docker kill jenkins-1.6 || true
	docker rm jenkins-1.6 || true
	docker rmi jenkins-1.6 || true

build:
	docker build -t jenkins-1.6 .

run:
	docker run --name jenkins-1.6 -p 8080:8080 -d jenkins-1.6

healthcheck:
	./healthcheck.sh

quality:
	./gradlew codenarcMain codenarcTest
