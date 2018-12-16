package solutions.linked.slds;

import org.apache.clerezza.commons.rdf.IRI;

/**
 * The ontology as java constants
 */
public class SLDS {
    //core properties
    public static final IRI port = new IRI("http://vocab.linked.solutions/slds#port");
    public static final IRI resourceDescriptionProvider = new IRI("http://vocab.linked.solutions/slds#resourceDescriptionProvider");
    public static final IRI javaClass = new IRI("http://vocab.linked.solutions/slds#javaClass");
    //iri translation
    public static final IRI iriTranslators = new IRI("http://vocab.linked.solutions/slds#iriTranslators");
    public static final IRI backendPrefix = new IRI("http://vocab.linked.solutions/slds#backendPrefix");
    public static final IRI frontendPrefix = new IRI("http://vocab.linked.solutions/slds#frontendPrefix");
    //GraphAndContext provider
    public static final IRI sparqlEndpoint = new IRI("http://vocab.linked.solutions/slds#sparqlEndpoint");
    public static final IRI userName = new IRI("http://vocab.linked.solutions/slds#userName");
    public static final IRI password = new IRI("http://vocab.linked.solutions/slds#password");
    public static final IRI enableVituosoWorkAround = new IRI("http://vocab.linked.solutions/slds#enableVituosoWorkAround");
    //FileResourceDescription provider
    public static final IRI resource = new IRI("http://vocab.linked.solutions/slds#resource");
    public static final IRI file = new IRI("http://vocab.linked.solutions/slds#file");
    
    
}
