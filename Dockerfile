FROM ubuntu:16.04

USER root
RUN apt-get update -y && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y curl
RUN mkdir /usr/share/jenkins && \
    curl  https://s3.amazonaws.com/edx-testeng-tools/jenkins/jenkins_1.651.3.war \
         -L -o /usr/share/jenkins/jenkins.war
EXPOSE 8080
ENV JENKINS_HOME /var/lib/jenkins
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000
RUN groupadd -g ${gid} ${group} \
    && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

RUN mkdir -p $JENKINS_HOME/init.groovy.d && \
    mkdir $JENKINS_HOME/utils && \
    mkdir $JENKINS_HOME/plugin-resolver
COPY src/main/groovy/*.groovy $JENKINS_HOME/init.groovy.d/
COPY utils/ $JENKINS_HOME/utils/
COPY plugin-resolver/ $JENKINS_HOME/plugin-resolver/
RUN chown -R ${user}:${group} $JENKINS_HOME

CMD /usr/bin/java -jar /usr/share/jenkins/jenkins.war --httpPort=8080
