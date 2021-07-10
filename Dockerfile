FROM adoptopenjdk/openjdk11:ubuntu
MAINTAINER gavinklfong@gmail.com

# install Node JS and json mock server
RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash - \
    && apt-get install -y nodejs \
    && npm install -g json-server

COPY mock-server/mock-data.json /app/mock-data.json

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080

WORKDIR /app

COPY docker-resources/entrypoint.sh /app/entrypoint.sh

CMD ["/app/entrypoint.sh"]

