SHELL := /usr/bin/env bash
.DEFAULT_GOAL := help
.PHONY: clean requirements plugins build logs e2e

help:
	@echo ''
	@echo 'Makefile for '
	@echo '     make help            show this information'
	@echo '     make clean           shutdown running container and delete image and clean workspace'
	@echo '     make clean.container shutdown running container and delete image'
	@echo '     make clean.ws        delete all groovy/gradle artifacts in the workspace'
	@echo '     make build           build a dockerfile for testing the jenkins configuration'
	@echo '     make run             run the dockerfile in the background'
	@echo '     make logs            tail the logs for the Jenkins container'
	@echo '     make shell           shell into the runnning Jenkkins container for debugging'
	@echo '     make healthcheck     run healthcheck script to test if Jenkins has successfully booted'
	@echo '     make quality         run codenarc on groovy source and tests'
	@echo '     make requirements    install requirements for acceptance tests'
	@echo '     make plugins         install specified Jenkins plugins and their dependencies'
	@echo '     make e2e             run python acceptance tests against a provisioned docker container'

clean: clean.container clean.ws

clean.container:
# run the following docker commands with '|| true' because they do not have a 'quiet' flag
	docker kill $(JENKINS_VERSION) || true
	docker rm $(JENKINS_VERSION) || true
	docker rmi $(JENKINS_VERSION) || true

clean.ws:
	./gradlew clean

build:
	docker build -t $(JENKINS_VERSION) --build-arg=CONFIG_PATH=$(CONFIG_PATH) \
		--build-arg=JENKINS_VERSION=$(JENKINS_VERSION) \
		--build-arg=JENKINS_WAR_SOURCE=$(JENKINS_WAR_SOURCE) .

run:
	docker run --name $(JENKINS_VERSION) -p 8080:8080 -d $(JENKINS_VERSION)

logs:
	docker logs -f $(JENKINS_VERSION)

shell:
	docker exec -it $(JENKINS_VERSION) /bin/bash

healthcheck:
	./healthcheck.sh

quality:
	./gradlew codenarcMain codenarcTest

requirements:
	./gradlew libs
	pip install -r test-requirements.txt

plugins:
	./gradlew -b plugins.gradle plugins

e2e:
	pytest
