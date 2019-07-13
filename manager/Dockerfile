FROM maven:3-jdk-8

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

ADD . /work
WORKDIR /work

RUN mvn package

CMD /wait && java -jar target/manager-0.0.1-SNAPSHOT.jar