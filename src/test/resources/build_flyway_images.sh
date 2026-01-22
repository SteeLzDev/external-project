#!/bin/bash

docker build -f src/test/resources/Dockerfile-econsig -t flyway-econsig src/test/resources/

docker build -f src/test/resources/Dockerfile-enomina -t flyway-enomina src/test/resources/
