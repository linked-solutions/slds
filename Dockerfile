FROM maven:3.6-jdk-8
RUN bash -c '([[ ! -d $JAVA_SECURITY_DIR ]] && ln -s $JAVA_HOME/lib $JAVA_HOME/conf) || (echo "Found java conf dir, package has been fixed, remove this hack"; exit -1)'
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean install -Pexecutable -DfinalName=slds
ENTRYPOINT  ["java", "-jar", "target/slds-executable.jar"]
CMD ["/config.ttl"]