FROM docker:27-cli AS cli
FROM jenkins/jenkins:lts-jdk21
USER root
RUN apt-get update && apt-get install -y --no-install-recommends make python3 && rm -rf /var/lib/apt/lists/*
COPY --from=cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=cli /usr/local/libexec/docker/cli-plugins/docker-buildx /usr/local/libexec/docker/cli-plugins/docker-buildx
USER jenkins
RUN jenkins-plugin-cli --plugins workflow-aggregator git credentials-binding timestamper
COPY init.groovy /usr/share/jenkins/ref/init.groovy.d/lesson.groovy
