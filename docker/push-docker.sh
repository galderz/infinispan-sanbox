#!/usr/bin/env bash

set -e -x

if [ -z "$1" ]
  then
    echo "No image id supplied"
fi

if [ -z "$2" ]
  then
    echo "No tag supplied"
fi

IMAGE_ID=$1
TAG=$2

docker tag $IMAGE_ID galderz/infinispan-server:$TAG
docker login
docker push galderz/infinispan-server
