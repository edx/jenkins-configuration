SHELL := /usr/bin/env bash
.DEFAULT_GOAL := help
.PHONY: clean requirements plugins build logs e2e show run

ifneq ($(http_proxy),)
DOCKER_BUILD_OPTS += --build-arg http_proxy=$(http_proxy) --build-arg HTTP_PROXY=$(http_proxy)
endif

ifneq ($(https_proxy),)
DOCKER_BUILD_OPTS += --build-arg https_proxy=$(https_proxy) --build-arg HTTPS_PROXY=$(https_proxy)
endif

ifneq ($(no_proxy),)
DOCKER_BUILD_OPTS += --build-arg no_proxy=$(no_proxy) --build-arg NO_PROXY=$(no_proxy)
endif

ifneq ($(http_proxy),)
JAVA_OPTS += -Dhttp.proxyHost=$(shell echo $(http_proxy) | sed 's/http:\/\///;s|\/.*||' | cut -d: -f1) -Dhttp.proxyPort=$(shell echo $(http_proxy) | sed 's/http:\/\///;s|\/.*||' | cut -d: -f2)
endif

ifneq ($(https_proxy),)
JAVA_OPTS += -Dhttps.proxyHost=$(shell echo $(https_proxy) | sed 's/http:\/\///;s|\/.*||' | cut -d: -f1) -Dhttps.proxyPort=$(shell echo $(https_proxy) | sed 's/http:\/\///;s|\/.*||' | cut -d: -f2)
endif

ifneq ($(no_proxy),)
# FIXME: substitution is not complete as UNIX's no_proxy use".mydomain.com" and Java's *.nonProxyHosts uses "*.mydomain.com"
comma:=,
JAVA_OPTS += -Dhttp.nonProxyHosts="$(subst $(comma),|,$(no_proxy))" -Dhttps.nonProxyHosts="$(subst $(comma),|,$(no_proxy))"
endif

help:
	@echo ''
	@echo 'Makefile for '
	@echo '     make help            show this information'
	@echo '     make clean           shutdown running container and delete image and clean workspace'
	@echo '     make clean.container shutdown running container and delete image'
	@echo '     make clean.ws        delete all groovy/gradle artifacts in the workspace'
	@echo '     make build           build a dockerfile for testing the jenkins configuration'
	@echo '     make run             run the dockerfile and kickoff jenkins'
	@echo '     make run.container   run the dockerfile without starting the jenkins .war'
	@echo '     make run.jenkins     run the jenkins .war on the container'
	@echo '     make logs            tail the logs for the Jenkins container'
	@echo '     make shell           shell into the runnning Jenkkins container for debugging'
	@echo '     make healthcheck     run healthcheck script to test if Jenkins has successfully booted'
	@echo '     make quality         run codenarc on groovy source and tests'
	@echo '     make requirements    install requirements for acceptance tests'
	@echo '     make plugins         install specified Jenkins plugins and their dependencies'
	@echo '     make show            show the versions of downloaded plugins'
	@echo '     make e2e             run python acceptance tests against a provisioned docker container'

clean: clean.container clean.ws

clean.container:
# run the following docker commands with '|| true' because they do not have a 'quiet' flag
	docker kill $(CONTAINER_NAME) || true
	docker rm $(CONTAINER_NAME) || true
	docker rmi $(CONTAINER_NAME) || true

clean.ws:
	./gradlew clean
	./gradlew -b plugins.gradle clean

build:
	docker build $(DOCKER_BUILD_OPTS) -t $(CONTAINER_NAME) --build-arg=CONFIG_PATH=$(CONFIG_PATH) \
		--build-arg=JENKINS_VERSION=$(JENKINS_VERSION) \
		--build-arg=JENKINS_WAR_SOURCE=$(JENKINS_WAR_SOURCE) .

run: run.container run.jenkins

run.container:
	docker run --name $(CONTAINER_NAME) -p 8080:8080 -p 2222:22 -d $(CONTAINER_NAME)

run.jenkins:
	docker exec -d -u jenkins ${CONTAINER_NAME} /usr/bin/java $(JAVA_OPTS) -jar /usr/share/jenkins/jenkins.war --httpPort=8080 --logfile=/var/log/jenkins/jenkins.log

logs:
	docker exec $(CONTAINER_NAME) tail -f /var/log/jenkins/jenkins.log

shell:
	docker exec -it $(CONTAINER_NAME) /bin/bash

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

show:
	./gradlew -b plugins.gradle show
