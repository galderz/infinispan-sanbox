# Use latest jboss/base-jdk:8 image as the base
FROM jboss/base-jdk:8

# Set the INFINISPAN_SERVER_HOME env variable
ENV INFINISPAN_SERVER_HOME /opt/jboss/infinispan-server

# Set the INFINISPAN_VERSION env variable
ENV INFINISPAN_VERSION 9.0.0.Final
ENV MGMT_USER admin

ENV MGMT_PASS admin 

ENV APP_USER user

ENV APP_PASS changeme

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

# Server download location
ENV DISTRIBUTION_URL https://repository.jboss.org/nexus/content/repositories/releases/org/infinispan/server/infinispan-server-build/$INFINISPAN_VERSION/infinispan-server-build-$INFINISPAN_VERSION.zip

# Download and extract the Infinispan Server
USER root

ENV HOME /opt/jboss/

ADD target/infinispan-server-9.0.1-SNAPSHOT /opt/jboss/infinispan-server
#Since we are playing with development versions, we don't care about the permissions
RUN  chmod -R 777 /opt/jboss/infinispan-server
RUN  chmod +x /opt/jboss/infinispan-server/bin/*.sh

USER 1000

# Copy entrypoint script
COPY docker-entrypoint.sh /usr/local/bin
COPY is_healthy.sh /usr/local/bin
COPY is_running.sh /usr/local/bin

ENTRYPOINT ["docker-entrypoint.sh"]

# Expose Infinispan server  ports 
EXPOSE 7600 8080 8181 8888 9990 11211 11222 57600
