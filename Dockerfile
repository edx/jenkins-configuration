FROM ubuntu:16.04

USER root
RUN apt-get update -y \
    && apt-get install -y openjdk-8-jdk \
    && apt-get install -y curl \
    && apt-get install -y git
ARG JENKINS_VERSION
ARG JENKINS_WAR_SOURCE
RUN mkdir /usr/share/jenkins \
    && curl "${JENKINS_WAR_SOURCE}/${JENKINS_VERSION}.war" \
         -L -o /usr/share/jenkins/jenkins.war
EXPOSE 8080
ENV JENKINS_HOME /var/lib/jenkins
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ARG CONFIG_PATH
ENV JENKINS_CONFIG_PATH /init-configs

ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000
RUN groupadd -g ${gid} ${group} \
    && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

RUN mkdir -p $JENKINS_HOME/init.groovy.d \
    && mkdir $JENKINS_HOME/plugins \
    && mkdir $JENKINS_HOME/utils
COPY src/main/groovy/*.groovy $JENKINS_HOME/init.groovy.d/
COPY plugins $JENKINS_HOME/plugins/
COPY utils/ $JENKINS_HOME/utils/
COPY ${CONFIG_PATH} init-configs/

RUN chown -R ${user}:${group} $JENKINS_HOME

CMD /usr/bin/java -jar /usr/share/jenkins/jenkins.war --httpPort=8080
