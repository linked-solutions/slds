# Simple LD Server

Expose Resources from a SPARQL Server as Linked Data.

This project shall provide a minimal usable extendable implementation.

## Building

    mvn install -P executable

this will create an executable jar e.g. `slds-1.0-SNAPSHOT.jar` in the target
directory.

## Running

Run the executable jar with one argument pointing to the configuration in a
turtle file, for example

    java -jar slds-1-SNAPSHOT.jar ../example-config.ttl

### Configuration

The configuration file is a Turtle file with one resource matching the IRI of
the location of the file, i.e. the resource which can be referred in Turtle as 
`<>`.

```
@prefix slds: <http://vocab.linked.solutions/slds#> .
```
Defining the `slds` prefix makes the rest of the file more compact and easier
to read.
```
<> 
    slds:port 5000 ;
    slds:sparqlEndpoint <https://lindasprd.netrics.ch:8443/lindas/query> .
```
The above defines the port on which SLDS shall listen and the SPARQL Endpoint
SLDS shall query. 

```
<>
    slds:iriTranslators (
        [
            slds:backendPrefix "http://treatment.plazi.org/"; 
            slds:frontendPrefix "http://localhost:5000/"
        ]
        [
            slds:backendPrefix "http://publication.plazi.org/"; 
            slds:frontendPrefix "http://127.0.0.1:5000/"
        ]) .
```
Unless the resources in the triple store already have the URIs under which they 
are to be published IRI-Translation rules are required. The above two additional
properties define how the prefixes in the IRIs served by SLDS (frontend-prefix) 
map to prefixes in the IRIs in the triple store (backend-prefix).

```
<https://lindasprd.netrics.ch:8443/lindas/query> 
    slds:userName "public" ;
    slds:password "public".
```
If the SPARQL endpoint needs authentication the username and password can be
specified as properties of the SPARQL Endpoint resource itself.