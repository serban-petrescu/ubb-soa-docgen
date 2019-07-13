FROM node:10

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

ADD . /work
WORKDIR /work

RUN npm ci

CMD /wait && npm start