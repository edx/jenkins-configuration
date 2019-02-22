# -- base image stage
# -- create a base image, containing the bulk of the configuration
# -- used for setting up a Jenkins server. Copy most of the groovy
# -- config scripts into the image. Due to the fact that some scripts
# -- will conflict with eachother, the base image will be used to
# -- create individual 'shard' containers, using Docker multi-stage
# -- builds. The scripts that create conflicts should be copied into
# -- these individual shard images.

FROM ubuntu:16.04 as base

USER root
RUN apt-get update -y \
    && apt-get install -y openjdk-8-jdk \
    && apt-get install -y curl \
    && apt-get install -y git \
    && apt-get install -y sudo \
    && apt-get install -y python-pip \
    && apt-get install -y openssh-server \
    && apt-get install -y vim

RUN mkdir /var/run/sshd
ADD ssh/jc_rsa.pub /root/.ssh/jc_rsa.pub
RUN cat root/.ssh/jc_rsa.pub >> ~/.ssh/authorized_keys \
    && chmod go-w ~/ && chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys
EXPOSE 22

ARG JENKINS_VERSION
ARG JENKINS_WAR_SOURCE
RUN mkdir /usr/share/jenkins \
    && curl "${JENKINS_WAR_SOURCE}/${JENKINS_VERSION}.war" \
         -L -o /usr/share/jenkins/jenkins.war
EXPOSE 8080
ENV JENKINS_HOME /var/lib/jenkins
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ARG CONFIG_PATH
ENV JENKINS_CONFIG_PATH $JENKINS_HOME/init-configs

ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000
RUN groupadd -g ${gid} ${group} \
    && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

RUN mkdir -p $JENKINS_HOME/init.groovy.d \
    && mkdir $JENKINS_HOME/plugins \
    && mkdir $JENKINS_HOME/utils \
    && mkdir $JENKINS_HOME/git \
    && mkdir -p /var/log/jenkins
COPY plugins $JENKINS_HOME/plugins/
COPY utils/ $JENKINS_HOME/utils/
COPY ${CONFIG_PATH} $JENKINS_HOME/init-configs/
COPY src/main/groovy/1addJarsToClasspath.groovy \
    src/main/groovy/2checkInstalledPlugins.groovy \
    src/main/groovy/3importCredentials.groovy \
    src/main/groovy/3installGroovy.groovy \
    src/main/groovy/3installPython.groovy \
    src/main/groovy/3mainConfiguration.groovy \
    src/main/groovy/3setGlobalProperties.groovy \
    src/main/groovy/3shutdownCLI.groovy \
    src/main/groovy/4configureEc2Plugin.groovy \
    src/main/groovy/4configureGHPRB.groovy \
    src/main/groovy/4configureGit.groovy \
    src/main/groovy/4configureGithub.groovy \
    src/main/groovy/4configureHipChat.groovy \
    src/main/groovy/4configureJobConfigHistory.groovy \
    src/main/groovy/4configureMailerPlugin.groovy \
    src/main/groovy/4configureMaskPasswords.groovy \
    src/main/groovy/4configureSecurity.groovy \
    src/main/groovy/4configureSlack.groovy \
    src/main/groovy/4configureSplunk.groovy \
    src/main/groovy/5addSeedJob.groovy \
    src/main/groovy/5configureEmailExtension.groovy \
    src/main/groovy/5createLoggers.groovy \
    $JENKINS_HOME/init.groovy.d/

# -- test shard #1
# -- copy the unique scripts used to configure a Jenkins container for
# -- running the tests specifed with the environment variable
# -- 'TEST_SHARD=shard_1'

FROM base as shard_1
COPY src/main/groovy/4configureGHOAuth.groovy \
    $JENKINS_HOME/init.groovy.d/
RUN chown -R jenkins:jenkins $JENKINS_HOME /var/log/jenkins

CMD ["/usr/sbin/sshd", "-D"]

# -- test shard #2
# -- copy the unique scripts used to configure a Jenkins container for
# -- running the tests specifed with the environment variable
# -- 'TEST_SHARD=shard_2'

FROM base as shard_2
COPY src/main/groovy/4configureSAML.groovy \
    src/main/groovy/3addUsers.groovy \
    $JENKINS_HOME/init.groovy.d/
RUN chown -R jenkins:jenkins $JENKINS_HOME /var/log/jenkins

CMD ["/usr/sbin/sshd", "-D"]
