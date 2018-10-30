#!/usr/bin/env bash

set -e -x

oc login -u developer -p developer
oc project myproject

APP=app

docker pull fabric8/s2i-java:2.3

# || true to make it idempotent
oc new-build \
  --binary \
  --strategy=source \
  --name=${APP} \
  -l app=${APP} \
  --docker-image="registry.hub.docker.com/fabric8/s2i-java:2.3" \
  || true

mvn clean package compile -DincludeScope=runtime

oc start-build ${APP} --from-dir=target/ --follow
