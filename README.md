# VSDS DEMONSTRATOR

The VSDS Demonstrator is a web application which can be used to demonstrate the ingestion/replication of Linked Data Event Streams.
The VSDS Demonstrator was built in the context of
the [VSDS project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) in order to easily exchange
open data.

## Structure of the VSDS Demonstrator
In the middle of the diagram, you can see both _backend_ as _VUE.js_, which will be referred to as the frontend. 
Those two components are the components of the full stack application. The frontend makes requests to the backend, which communicate with both the PostgreSQL db as the Eclipse RDF4J graph db. 

At the left of the diagram, you can see a LDIO component, which is a LDIO-Orchestrator and will be further referred to as the data provider. The data provider will follow one or more Linked Data Event Streams, and will eventually not use only use the LdioHttpOut to POST the member to the backend, but also the LdioRepositoryMaterialiser to add the member to the graph db. 
![img.png](documentation/img.png)

## How To Run
We'll show you how to run the VSDS Demonstrator, both locally via Maven and Docker.

### Locally - Maven
To let the VSDS Demonstrator run successfully, there are some requirements.
1. In the [resources](backend/src/main/resources) folder in the backend, a `application.yml` file should be provided, which can look like the [following example](backend/examples/example-application.yaml)
2. An up and running instance of a PostgreSQL is needed, and it's connection details should be provided in the `application.yml` file.
3. An up and running instance of a [RDF4J Server and Workbench is needed](https://rdf4j.org/documentation/tools/server-workbench), it's connection details should also be provided in the `application.yaml` file.

If the following requirements are met, the VSDS Demonstrator can be started with following commando's:
```shell
mvn clean install
```
```shell
cd ./backend
```
```shell
cd mvn spring-boot:run
```

### Docker
There are 3 files where you can configure the dockerized application:

- [The demonstrator config files](#the-demonstrator-config-files)
- [The data provider config files](#the-data-provider-config-files)
- [The docker compose file](#the-docker-compose-file)

#### The Demonstrator Config Files

Runtime settings can be defined via `.env` files. Alternatively, an `application.yml` file can be mounted into the container.

The [`demonstrator.env`](./docker-compose/demonstrator.env) is an `.env` example file.

#### The Data Provider Config Files
The data provider is an instance of a LDIO-orchestrator, which also can be configured via an `.env` file or a mounted `application.yml` file. 
For this container, we chose to go with the second scenario. More information about how to configure the LDIO-Orchestrator 
can be found in the [LDIO Documentation](https://informatievlaanderen.github.io/VSDS-Linked-Data-Interactions/).

The [`data-provider.config.yml`](./docker-compose/data-provider.config.yml) is an `application.yml` example file.

#### The docker compose File
Modify the [`docker-compose.yml`](./docker-compose.yml) according to your needs. To start the containers, run the following commands.

Run the containers in the services
```shell
docker compose up
```