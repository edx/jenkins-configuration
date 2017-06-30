FROM ubuntu:16.04

USER root
RUN apt-get update -y && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y curl && \
    apt-get install -y unzip
RUN mkdir /usr/share/jenkins && \
    curl  https://s3.amazonaws.com/edx-testeng-tools/jenkins/jenkins_1.651.3.war \
         -L -o /usr/share/jenkins/jenkins.war
EXPOSE 8080
ENV JENKINS_HOME /var/lib/jenkins
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV JENKINS_UC https://updates.jenkins.io

ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000
RUN groupadd -g ${gid} ${group} \
    && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

RUN mkdir -p $JENKINS_HOME/init.groovy.d
COPY src/main/groovy/*.groovy $JENKINS_HOME/init.groovy.d/
RUN chown -R ${user}:${group} $JENKINS_HOME

COPY jenkins-support /usr/local/bin/jenkins-support
COPY jenkins.sh /usr/local/bin/jenkins.sh
COPY install-plugins.sh /usr/local/bin/install-plugins.sh
COPY test_data/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN bash /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
RUN cp -R /usr/share/jenkins/ref/plugins /var/lib/jenkins/
CMD /usr/bin/java -jar /usr/share/jenkins/jenkins.war --httpPort=8080
