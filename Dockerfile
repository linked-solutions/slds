FROM maven:3.2-jdk-8
MAINTAINER Reto Gmür <reto@factsmission.com>

EXPOSE 5000

#Prepare
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Build
COPY ./ /usr/src/app

RUN mvn install -DfinalName=slds -P executable

ENTRYPOINT ["java"]
CMD ["-jar", "target/slds.jar", "wikidata-config.ttl"]