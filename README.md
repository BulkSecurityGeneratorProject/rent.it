# README #
Rent.IT Project
System for renting/lending anything you want.

## HTTP://WORKINGBRICKS.COM

Livecoding.TV<img src="https://tools.livecoding.tv/badge/statusSmall/1/opastukhov?maxAge=25920" width="12" height="5"/>

[![GitHub release](https://img.shields.io/github/tag/picaro/rent.it.svg?maxAge=2592000)](https://github.com/picaro/rent.it/tags)
[![Dependency Status](https://gemnasium.com/badges/github.com/WorkingBricks/rent.it.svg?maxAge=259200)](https://gemnasium.com/github.com/WorkingBricks/rent.it)
[![Code Climate](https://codeclimate.com/github/WorkingBricks/rent.it/badges/gpa.svg?maxAge=259200)](https://codeclimate.com/github/WorkingBricks/rent.it)
[![Issue Count](https://codeclimate.com/github/WorkingBricks/rent.it/badges/issue_count.svg?maxAge=259200)](https://codeclimate.com/github/WorkingBricks/rent.it)

[![Prod Up Time](https://i.h-t.co/dns%20test.png?id=67ad7bf4-8641-4dbb-bfd9-def218a62441)](http://www.host-tracker.com/UptimeGraph/UptimeInfo/67ad7bf4-8641-4dbb-bfd9-def218a62441)

## Mindmap with ideas
https://www.mindmup.com/#m:a11b08d3802ace013411251d36ac3dd6cf  V1

## Mockups
https://mockingbot.com/workspace#apps/p73F8076A6D1471496443580 
** ASK form password

## Build
1) With environment in the Docker
./mvnw clean package -Pprod docker:build

2) Standalone
mvn clean install

mvn clean install -DskipTests

# Run 
1) with environment ***
docker-compose -f src/main/docker/app.yml up
This command will start up your application and the services it relies on (database, search engine, JHipster Registry…).

2) Standalone
java -jar target/*.war --spring.profiles.active=prod 

* ASK for environment variables

## Project Tasks
https://trello.com/b/yLbmjvrm/rent-it

### CI server
[ ![Codeship Status for picaro/rent.it](https://codeship.com/projects/a9c60310-f8fa-0133-a8ad-268d110da048/status?branch=master)](https://codeship.com/projects/151098)

### Remote shell CRaSH
ssh -p 2000 user@localhost
password from logs
help - to see all commands
you could add custom commands

### Spring Boot Actuator
* <http://localhost:8080/actuator/#/actuator>
you could add custom metrics

### Code coverage
[![Coverage Status](https://coveralls.io/repos/github/WorkingBricks/rent.it/badge.svg?branch=master)](https://coveralls.io/github/WorkingBricks/rent.it?branch=master)
[![codecov](https://codecov.io/gh/WorkingBricks/rent.it/branch/master/graph/badge.svg)](https://codecov.io/gh/WorkingBricks/rent.it)

For 17.08.2016 by IDEA

class	68% (103/150)	
method  63% (503/797)	
line    59% (1683/2849)

mvn clean test jacoco:report coveralls:report


UI Coverage: 
[![Test Coverage](https://codeclimate.com/github/WorkingBricks/rent.it/badges/coverage.svg)](https://codeclimate.com/github/WorkingBricks/rent.it/coverage)

gulp test

codeclimate-test-reporter < target/test-results/coverage/report-lcov/lcov.info  

### Rest API Documents

* <http://localhost:8080/swagger-ui/index.html> - API Description

### What is this repository for? ###
Application to lend any stuff.

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

Minimum requirements 
* Java 8
* Maven 3

### LICENSE 
[![License (CC BY-NC-ND)](https://github.com/picaro/rent.it/blob/master/logos/by-nc-nd.png)](http://creativecommons.org/licenses/by-nc/4.0/)

