[![Build Status](https://travis-ci.org/OlegEfrem/bank-account.svg?branch=master)](https://travis-ci.org/OlegEfrem/bank-account)

# About
* This is a Bank Account system implementation of the requirements described [here](Assignment.pdf);

# Highlights
## Libraries, Frameworks & Plugins
* Dependencies are defined [here](build.sbt) and 
plugins [here](/project/plugins.sbt);
* Rest API based on [akka-http](https://doc.akka.io/docs/akka-http/10.1.7/introduction.html?language=scala);
* For json (de)serialization [jackson-scala](https://github.com/FasterXML/jackson-module-scala) is used;
* Testing layer uses: [scala test](http://www.scalatest.org/) for defining test cases, [scala mock](http://scalamock.org/) for mocking dependencies in unit tests and 
[akka-http-test-kit](https://doc.akka.io/docs/akka-http/10.1.7/routing-dsl/testkit.html?language=scala) for api tests;
* Plugins configured for the project are: [s-coverage](https://github.com/scoverage/sbt-scoverage) for code test coverage, [scala-style](http://www.scalastyle.org/) for code style checking,
[scalafmt](https://scalameta.org/scalafmt/) for code formatting and [sbt-updates](https://github.com/rtimush/sbt-updates) for keeping up the dependencies up to date 

# API Behaviour
It's behaviour is defined by the API Integration test found [here](/src/test/scala/com/oef/bank/account/infrastructure/inbound/http/RestApiTest.scala).
## The test output is: 
```aidl
[info] RestApiTest:
[info] restApi should
[info] - create a new account responding with HTTP-201
[info] - return an existing account
[info] - delete an existing account
[info] - deposit money to an existing account
[info] - withdraw money from an existing account
[info] - transfer money from an existing account to another existing account
[info] - respond with HTTP-404 Not Found for a non existing path
[info] - respond with HTTP-405 Method Not Allowed for a non supported HTTP method
[info] - respond with HTTP-400 Bad Request in case of an  (pending)
[info] - respond with HTTP-502 Bad Gateway in case of a  (pending)
[info] - respond with HTTP-404 Not Found in case of a  (pending)
[info] - respond with HTTP-500 Internal Server Error in case of a generic Exception

```
## Run application
To run application, call:
```
sbt run
```
If you wanna restart your application without reloading of sbt, use:
```
sbt re-start
```