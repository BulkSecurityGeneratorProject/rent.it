# README #
Rent.IT Project
System for renting anything.


[![GitHub release](https://img.shields.io/github/tag/picaro/rent.it.svg?maxAge=2592000)](https://github.com/picaro/rent.it/tags)
[![Dependency Status](https://gemnasium.com/badges/github.com/WorkingBricks/rent.it.svg)](https://gemnasium.com/github.com/WorkingBricks/rent.it)
[![Code Climate](https://codeclimate.com/github/WorkingBricks/rent.it/badges/gpa.svg)](https://codeclimate.com/github/WorkingBricks/rent.it)

## Mindmap with ideas
https://www.mindmup.com/#m:a11b08d3802ace013411251d36ac3dd6cf  V1

## Build
1) With environment in the Docker
./mvnw clean package -Pprod docker:build

2) Standalone
mvn clean install

# Run 
1) with environment ***
docker-compose -f src/main/docker/app.yml up
This command will start up your application and the services it relies on (database, search engine, JHipster Registry…).

2) Standalone
java -jar target/*.war --spring.profiles.active=prod 

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
[![Coverage Status](http://coveralls.io/repos/github/picaro/rent.it/badge.svg?branch=master)](https://coveralls.io/github/picaro/rent.it?branch=master)

mvn cobertura:cobertura coveralls:report
https://codecov.io/gh/picaro/rent.it

### Rest API Documents

* <http://127.0.0.1:8080/swagger-ui.html> - API Description
* <http://127.0.0.1:8080/browser/index.html> - API tester

### What is this repository for? ###
Application to lend any stuff.

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### LICENSE 
[![License (CC BY-NC-ND)](https://github.com/picaro/rent.it/blob/master/logos/by-nc-nd.png)](http://creativecommons.org/licenses/by-nc/4.0/)
