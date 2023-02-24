# uab-app Project
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```
If an error appeared related to dependencies please run the following command
and wait for the dependencies to be downloaded:
```shell script
./mvnw clean install
```
> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Running the tests
To run the tests we can use the maven from quarkus by running the following command:
```shell script
./mvnw test
```
To run a single class test:
```shell script
./mvnw -Dtest=ClassName test
```
## To test the application using postgres check the following:
Make sure you have docker installed.
In application.properties file in src/main/resources folder:
1. Uncomment quarkus.datasource.db-kind = postgresql in order to use postgres db
2. Comment quarkus.datasource.jdbc.url = jdbc:h2:mem:uabdb to stop using in memory db.
3. To connect to the db and check the tables the following config can be used: 
- uncomment quarkus.datasource.devservices.port=49161
- host-> localhost
- port-> 49161
- user-> quarkus
- password -> quarkus
- URL -> jdbc:postgresql://localhost:49161/quarkus
- 
## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/uab-app-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Another option to run this project is to install quarkus

- The tutorial to install quarkus can be found at: ([guide](https://quarkus.io/get-started/)): Get started with quarkus

After installing quarkus we can simply run in the project root:
```shell script
quarkus dev
```
## Related Guides

- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing JAX-RS and more
- RESTEasy Classic Qute ([guide](https://quarkus.io/guides/qute)): Qute Templating integration for RESTEasy
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)

### RESTEasy Qute

Create your web page using Quarkus RESTEasy & Qute

[Related guide section...](https://quarkus.io/guides/qute#type-safe-templates)

---
## Project views
### Basic User view:
![alt text](../Project%20Images/Basic%20user%20-%20bikes%20view.png)
![alt text](../Project%20Images/Basic%20user%20-%20cart%20view.png)
![alt text](../Project%20Images/Basic%20user%20-%20buy%20checkout%20view.png)

![alt text](../Project%20Images/Basic%20user%20-%20order%20view.png)
![alt text](../Project%20Images/Basic%20user%20-%20bikes%20view%20after%20order%20placed.png)
![alt text](../Project%20Images/Basic%20user%20-%20order%20history%20view.png)
---
<pre>
</pre>
### Manager view:
![alt text](../Project%20Images/Manager%20-%20bike%20view.png)
![alt text](../Project%20Images/Manager%20-%20edit%20bike%20view.png)
![alt text](../Project%20Images/Manager%20-%20after%20edit%20update,%20view.png)

