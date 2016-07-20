FROM java:openjdk-8-jre-alpine

# add directly the war
ADD rentit*.jar /rentit.jar

RUN sh -c 'touch /rentit.jar'
VOLUME /tmp
EXPOSE 8081 5701/udp
EXPOSE 8080 5701/udp
CMD echo "The application is stating..." && \
    java -Djava.security.egd=file:/dev/./urandom -jar /rentit.jar
