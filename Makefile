SHELL := /usr/bin/env bash
.DEFAULT_GOAL := help
.PHONY: clean requirements plugins build logs e2e show run upgrade

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

stop.container:
	docker kill $(CONTAINER_NAME) || true
	docker rm $(CONTAINER_NAME) || true

clean.container: stop.container
	docker rmi $(CONTAINER_NAME) || true

clean.ws:
	./gradlew clean
	./gradlew -b plugins.gradle clean

build:
	docker build -t $(IMAGE_NAME) --no-cache\
                --build-arg=CONFIG_PATH=$(CONFIG_PATH) \
		--build-arg=JENKINS_VERSION=$(JENKINS_VERSION) \
		--build-arg=JENKINS_WAR_SOURCE=$(JENKINS_WAR_SOURCE) \
		--target=$(TEST_SHARD) .

run: run.container run.jenkins

run.container: stop.container
	docker run --name $(CONTAINER_NAME) -p 127.0.0.1:8080:8080 -p 127.0.0.1:2222:22 -d $(IMAGE_NAME)

run.jenkins:
	docker exec -d -u jenkins ${CONTAINER_NAME} /usr/bin/java -jar /usr/share/jenkins/jenkins.war --httpPort=8080 --logfile=/var/log/jenkins/jenkins.log

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
	pip install -r requirements/ci.txt

get-password:
	docker exec ${CONTAINER_NAME} cat /var/lib/jenkins/secrets/initialAdminPassword

# Define PIP_COMPILE_OPTS=-v to get more information during make upgrade.
PIP_COMPILE = pip-compile --rebuild --upgrade $(PIP_COMPILE_OPTS)

COMMON_CONSTRAINTS_TXT=requirements/common_constraints.txt
.PHONY: $(COMMON_CONSTRAINTS_TXT)
$(COMMON_CONSTRAINTS_TXT):
	wget -O "$(@)" https://raw.githubusercontent.com/edx/edx-lint/master/edx_lint/files/common_constraints.txt || touch "$(@)"
	echo "$(COMMON_CONSTRAINTS_TEMP_COMMENT)" | cat - $(@) > temp && mv temp $(@)

upgrade: export CUSTOM_COMPILE_COMMAND=make upgrade
upgrade: $(COMMON_CONSTRAINTS_TXT)
	## update the requirements/*.txt files with the latest packages satisfying requirements/*.in
	pip install -qr requirements/pip-tools.txt
	# Make sure to compile files after any other files they include!
	$(PIP_COMPILE) --allow-unsafe --rebuild -o requirements/pip.txt requirements/pip.in
	$(PIP_COMPILE) -o requirements/pip-tools.txt requirements/pip-tools.in
	pip install -qr requirements/pip.txt
	pip install -qr requirements/pip-tools.txt
	$(PIP_COMPILE) -o requirements/test.txt requirements/test.in
	$(PIP_COMPILE) -o requirements/ci.txt requirements/ci.in
	$(PIP_COMPILE) -o requirements/dev.txt requirements/dev.in

plugins:
	./gradlew -b plugins.gradle plugins

e2e:
	tox

show:
	./gradlew -b plugins.gradle show

show.container:
	docker exec -w /var/lib/jenkins/git/jenkins-configuration -e PLUGIN_OUTPUT_DIR=/var/lib/jenkins/plugins ${CONTAINER_NAME} ./gradlew -b plugins.gradle show
