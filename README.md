# Resources
* [ELO](https://www.geeksforgeeks.org/elo-rating-algorithm)
* [Heroku (Kotling based)](https://devcenter.heroku.com/articles/getting-started-with-kotlin)
* [Github](https://devcenter.heroku.com/articles/github-integration) 
* Travis CI
* [RestFul API](https://medium.com/@dime.kotevski/writing-a-restful-backend-using-kotlin-and-spring-boot-9f162c96e428)
* [Create a react app](https://developer.okta.com/blog/2018/07/19/simple-crud-react-and-spring-boot)
* [Create dynamic forms](https://react-jsonschema-form.readthedocs.io/en/latest/)
* [Spring + react](https://spring.io/guides/tutorials/react-and-spring-data-rest/)
* [Java on Heroku](https://devcenter.heroku.com/categories/java)

# Build status

[![Build Status](https://travis-ci.com/akustik/topscores.svg?branch=master)](https://travis-ci.com/akustik/topscores)

# Continuous deployment

Master changes are automatically grabbed by Heroku and deployed.

# Documentation

Rest API documentation can be found at http://<host>/swagger-ui.html

# Settings

## Accounts

Use token:account=pwd as an env variable to enable a new account

```
$ heroku config
$ heroku config:set token:account=pwd
```

## Slack

The slack integration makes sure that the commands are issued by the slack application by checking the signature. 
Only teams with a configured token are allowed.
```
token:patxanga=<required for the patxanga slack team>
slack_client_id=<something>
slack_client_secret=<something>
slack_signing_secret=<something>
```

For development environments the signature validation can be bypassed by adding an environment flag
```
bypass_slack_secret=true
```

Create an application and set up the following URL for the command integration,
```
https://something-nice-34567.herokuapp.com/slack/command
```

## Running Locally

Use docker-compose to run the application locally,

```sh
$ docker-compose up --build
```

## Deploying to Heroku

Master branch is integrated with Heroku and it is deployed automatically.
