# Simple LD Server

Expose Resources from a SPARQL Server as Linked Data.

This project shall provide a minimal usable extendable implementation and serve
as an example.

## Building and running

You can build slds directly with [maven](https://maven.apache.org/) or use [Docker](https://docker.com). 

### Maven

Run the following command:

    mvn install -P executable

this will create an executable jar e.g. `slds-1.0.0-SNAPSHOT.jar` in the target
directory.

Run the executable jar with one argument pointing to the configuration in a
turtle file, for example

    java -jar slds-1.0.0-SNAPSHOT-executable.jar ../example-config.ttl

### Using Docker

You can build a docker image named `slds` with

    docker build -t slds .

The image will launch slds with `/config.ttl` as configuration by default. The easiest way to start it with you own config is to mount a file from your local filesystem at that location. You can then run the image with something like:

    docker run -ti -p 5000:5000 -v C:\Users\me\path\to\config.ttl:/config.ttl slds 


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
    slds:resourceDescriptionProvider [
        slds:javaClass "solutions.linked.slds.providers.GraphAndContext";
        slds:sparqlEndpoint <https://lindasprd.netrics.ch:8443/lindas/query> 
    ].
```
The above defines the port on which SLDS shall listen and the SPARQL Endpoint
SLDS shall query. 

```
<> 
    slds:port 5000 ;
    slds:resourceDescriptionProvider [
        slds:javaClass "solutions.linked.slds.providers.GraphAndContext";
        slds:sparqlEndpoint <https://lindasprd.netrics.ch:8443/lindas/query> 
        slds:iriTranslators (
            [
                slds:backendPrefix "http://treatment.plazi.org/"; 
                slds:frontendPrefix "http://localhost:5000/"
            ]
            [
                slds:backendPrefix "http://publication.plazi.org/"; 
                slds:frontendPrefix "http://127.0.0.1:5000/"
            ]) 
    ].
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

### SSL Certificates

SLDS conatains code to accept any SSL certificate the SPARQL Server (or the man
in the middle) uses. Override `ConfigUtils.createHttpClient()` for some serious
security.  