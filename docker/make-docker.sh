#!/usr/bin/env bash

set -e -x

INFINISPAN_HOME=~/0/infinispan/git
VERSION=9.0.1-SNAPSHOT
SERVER_ZIP=server/integration/build/target/infinispan-server-${VERSION}

rm -drf target
mkdir target
rm -drf target/infinispan-server-${VERSION}
cp -r $INFINISPAN_HOME/$SERVER_ZIP target

docker build -t infinispan-server .
