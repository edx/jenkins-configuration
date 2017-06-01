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
CMD /usr/bin/java -jar /usr/share/jenkins/jenkins.war --httpPort=8080
