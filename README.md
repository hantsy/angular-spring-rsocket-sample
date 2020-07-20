# angular-spring-rsocket-sample

This sample is to demonstrate a chat application using the following cutting-edge technology stack :

* Angular as client which uses `rsocket-js` to communicate with the server side
* Spring WebFlux based RSocket server which uses WebSocket as transport protocol
* Spring Data MongoDB based  `@Tailable`  query result as an infinite stream

## Prerequisites

* NodeJS  14
* OpenJDK 14
* Docker for Windows/MacOS

## Build

Before running the application, you should build and run client and server side respectively.

### Server

Run a MongoDB service firstly, simply you can run it from a Docker container. There is a `docker-compose.yaml` file is ready for you.

```bash
docker-compose up mongodb
```

Build the application.

```e
./gradlew build
```

Run the target jar from the *build* folder to start up the application.

```bash
java -jar build/xxx.jar
```

### Client

Install dependencies.

```bash
npm install
```

Start up the application.

```bash
npm run start
```

Open a browser and  navigate to http://localhost:4200.


## Reference

* [rsocket-js samples](https://github.com/rsocket/rsocket-js/blob/master/packages/rsocket-examples)
* [RSocket With Spring Boot + JS: Zero to Hero ](https://dzone.com/articles/rsocket-with-spring-boot-amp-js-zero-to-hero)
