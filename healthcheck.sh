#!/usr/bin/env bash

# Run a curl against the Jenkins instance installed in a Dockerfile
# to do a basic health check

set -e
set -x

echo "Wait 30 seconds for Jenkins to boot"
sleep 30

echo "Curling against the Jenkins server"
STATUS_CODE=$(curl -sL -w "%{http_code}" localhost:8080 -o /dev/null)

if [[ "$STATUS_CODE" == "200" ]]; then
    echo "Jenkins has come up and ready to use"
    exit 0
else
    echo "Jenkins did not return a correct status code."
    echo "Returned: $STATUS_CODE"
    exit 1
fi
