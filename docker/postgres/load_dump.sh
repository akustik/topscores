#!/bin/bash
set -e

pg_restore --username "$POSTGRES_USER" -d topscores /docker-entrypoint-initdb.d/db.dump