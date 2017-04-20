Build Infinispan:

    cd ~/0/infinispan/git
    mvn clean && mvn -DskipTests=true clean install

Build docker image:

    cd docker
    ./make-docker.sh
    ...
    Successfully built e2cd0de3e0a0
    
Use the docker image id to push it:
 
    ./push-docker.sh e2cd0de3e0a0 9.0.x-1e092e9e993c 
