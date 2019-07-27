start-local:
	./gradlew -x test build; heroku local

show-local-tables:
	psql -c "\dt"

load-dump-locally:
	pg_restore -Ocd guillemmercadaldiaz latest.dump

prod-db:
	heroku pg:psql

prod-db-info:
	heroku pg:info

prod-db-show-tables:
	heroku pg:psql -c "\dt"

prod-db-backup:
	heroku pg:backups:capture
	heroku pg:backups:download

prod-env:
	heroku config
	
prod-redis-maxmemory-policy:
	heroku redis:maxmemory --policy allkeys-lru
  
prod-redis-info:
	heroku redis:info
	
prod-redis-cli:
	heroku redis:cli