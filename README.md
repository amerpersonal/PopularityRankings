# About

Popularity rankings is the app made for cakculating statistics for products based in their ratings. It consists of two programs:
- Runner, to run calculations from command line
- WebServer, to start a web server with single route, which enabled us to pass valid CSV file in request body and get JSON output containing calculated statistics.

# Getting started

Clone this repo, install needed tools and run it (see below)

## Prerequisites

* Java 1.8
* SBT 0.13.5 or higher
* Scala 2.12

If you're on Mac, to install those tools use Homebrew. Another option is to use Docker containers.

## Installing and running

After cloning project, navigate to root directory and pack FAT jar (with all dependencies) with

```
sbt assembly
```

This task will also execute spec tests. Then following options are available:

### Starting program from command line

```
java -cp target/scala-2.12/PopularityRankings-assembly-0.1.jar Runner <path to CSV file>
```

### Starting server from command line

```
java -cp target/scala-2.12/PopularityRankings-assembly-0.1.jar api.WebServer
```

### Running from IDE

Another way of running locally is to import project in IntelliJ (or IDE or your choice).
To do this follow the next procedure:
 * import project as SBT project
 * refresh project in SBT tool window to make all sbt dependencies available
 * create run configuration 
 * run project with created configuration

### Running with Docker

For running with Docker, we first need to create Dockerfile on project root. Then build image:

 ``` docker build -t <image_name> . ```

 Then run container with:

 ```docker run -p 8080:8080 -t -i <image_name>```

 Server shoould be accessible on port 8080.

# Configuration

There are no specific configurations needed to run command line program or server.

# Tests

Run `sbt test` task from SBT console or create test configuration inside favourite IDE and run it. Project is containing both unit tests and API routes tests. Not all functions and scenarios are convered with tests currently.

# Deployment

To deploy the program or server, one should create Dockerfile and Jenkinsefile with appropriate build and run steps (check [Installing and running_section](https://github.com/amerpersonal/PopularityRankings/blob/master/README.md#installing-and-running))


# Built With

* [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/) - library for creating APIs in Scala using Akka stack
* [SBT](http://www.scala-sbt.org/) - Dependency Management

# Implementations and performances

Currently there are 3 implementations of calculation running:

- naive implementation that uses Scala collection chaining, without paying to much attention to performances ([CollectionChainingRanker](https://github.com/amerpersonal/PopularityRankings/blob/master/src/main/scala/rankings/CollectionChainingRanker.scala))
- implementation using recursion ([RecursionRanker](https://github.com/amerpersonal/PopularityRankings/blob/master/src/main/scala/rankings/RecursionRanker.scala))
- implementation using fold ([FoldRanker](https://github.com/amerpersonal/PopularityRankings/blob/master/src/main/scala/rankings/FoldRanker.scala))

During first run in IntelliJ (all caches clear), implementations showed the following perormance:

```
Statistics calculated using collection chaining in 1098 ms: {"bestRatedProducts":["blu-ray-01","fixie-01","widetv-03"],"invalidLines":1,"lessRatedProduct":"saddle-01","mostRatedProduct":"wifi-projector-01","validLines":48,"worstRatedProducts":["endura-01","smarttv-01","patagonia-01"]}
```
```
Statistics calculated using recursion in 408 ms: {"bestRatedProducts":["blu-ray-01","fixie-01","widetv-03"],"invalidLines":2,"lessRatedProduct":"saddle-01","mostRatedProduct":"wifi-projector-01","validLines":48,"worstRatedProducts":["endura-01","smarttv-01","patagonia-01"]}
```
```
Statistics calculated using fold in 474 ms: {"bestRatedProducts":["blu-ray-01","fixie-01","widetv-03"],"invalidLines":2,"lessRatedProduct":"saddle-01","mostRatedProduct":"wifi-projector-01","validLines":48,"worstRatedProducts":["endura-01","smarttv-01","patagonia-01"]}
```

# API specification

API contains only one route to make testing easier (through Postman or any other tool of your choice).

```
curl --location --request POST 'http://localhost:8080/api/v1/statistics' \
--form 'csv=@"<path to your CSV file>"'
```

Response body

```
{
    "bestRatedProducts": [
        "blu-ray-01",
        "fixie-01",
        "widetv-03"
    ],
    "invalidLines": 1,
    "lessRatedProduct": "saddle-01",
    "mostRatedProduct": "wifi-projector-01",
    "validLines": 48,
    "worstRatedProducts": [
        "endura-01",
        "smarttv-01",
        "patagonia-01"
    ]
}
```

For schema validation, we can use some online schema validator, such as [this](https://www.liquid-technologies.com/online-json-schema-validator)

# Status and further work

This project contains only a POC, with a huge room for improvement. If we want to Scale the process so it works in reasonable amound of time for big files and streams, these are some of the options we can use:

- use FS2 library that is meant for working with streams
- use Apache Spark
- experiment with other data structures, such as BinaryTree or similar which may be more suitable for sorting hugh data sets
- use more optimised algorithms for sorting/searching in the process, based on the data types and structures

To write a code more in a functional way, we may use Typelevel stack (cats.io and http4s) instead of Lightbend(Akka).


