# Simple LD Server

Expose Resources from a SPARQL Server as Linked Data.

This project shall provide a minimal usable extendable implementation.

## Building

    mvn install

this will create an executable jar e.g. `slds-1.0-SNAPSHOT.jar` in the target
directory.

## Running

Run the executable jar with one argument pointing to the configuration in a
turtle file, for example

    java -jar slds-1-SNAPSHOT.jar ../example-config.ttl
