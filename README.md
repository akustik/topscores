# Resources
* [Heroku (Kotling based)](https://devcenter.heroku.com/articles/getting-started-with-kotlin)
* [Github](https://devcenter.heroku.com/articles/github-integration) 
* Travis CI
* [RestFul API](https://medium.com/@dime.kotevski/writing-a-restful-backend-using-kotlin-and-spring-boot-9f162c96e428)

# Build status

[![Build Status](https://travis-ci.com/akustik/topscores.svg?branch=master)](https://travis-ci.com/akustik/topscores)

# Continuous deployment

Master changes are automatically grabbed by Heroku and deployed.

## Running Locally

Make sure you have Java and Maven installed.  Also, install the [Heroku CLI](https://cli.heroku.com/).

```sh
$ git clone https://github.com/heroku/kotlin-getting-started.git
$ cd kotlin-getting-started
$ mvn install
$ heroku local:start
```

Your app should now be running on [localhost:5000](http://localhost:5000/).

If you're going to use a database, ensure you have a local `.env` file that reads something like this:

```
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/java_database_name
```

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

## Documentation

For more information about using Java and Kotlin on Heroku, see these Dev Center articles:

- [Java on Heroku](https://devcenter.heroku.com/categories/java)
