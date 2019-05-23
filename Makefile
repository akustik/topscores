start-local:
	./gradlew build; heroku local

show-local-tables:
	psql -c "\dt"

prod-db:
	heroku pg:psql

prod-db-info:
	heroku pg:info

prod-db-show-tables:
	heroku pg:psql -c "\dt"

prod-env:
	heroku config
