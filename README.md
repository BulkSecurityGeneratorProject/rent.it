# README #
Rent.IT Project
System to rent any thing.
[![GitHub release](https://img.shields.io/github/release/picaro/rent.it.svg?maxAge=2592000)](https://github.com/picaro/rent.it/releases)


## Project
https://trello.com/b/mT3x7tcr/lend-it-tasks

### CI server
[ ![Codeship Status for intercity/chef-repo](https://codeship.io/projects/a9c60310-f8fa-0133-a8ad-268d110da048/status)](https://codeship.io/projects/80440)

### Sonar
docker-compose -f src/main/docker/sonar.yml up

### Remote shell CRaSH
ssh -p 2000 user@localhost
password from logs
help - to see all commands
you could add custom commands

### Spring Boot Actuator
* <http://localhost:8080/actuator/#/actuator>
* <http://localhost:8080/health>
* <http://localhost:8080/metrics> 
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
[![License (CC BY-NC-ND)](https://img.shields.io/badge/license-Creative%20Commons%20BY-NC-ND%204.0-blue.svg?style=flat-square)](http://creativecommons.org/licenses/by-nc/4.0/)