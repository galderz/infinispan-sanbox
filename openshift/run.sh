#!/usr/bin/env bash

set -e

oc delete all --selector=run=app || true

getImageName() {
  oc get is app  -o jsonpath="{.status.dockerImageRepository}"
}

oc run app \
  --image=$(getImageName) \
  --replicas=1 \
  --restart=OnFailure


getPodStatus() {
  oc get pod -l run=app -o jsonpath="{.items[0].status.phase}"
}

status=NA
while [ "$status" != "Running" ];
do
  status=$(getPodStatus)
  echo "Status of pod: ${status}"
  sleep .5
done


getPodName() {
  oc get pod -l run=app -o jsonpath="{.items[0].metadata.name}"
}

oc logs $(getPodName) -f
