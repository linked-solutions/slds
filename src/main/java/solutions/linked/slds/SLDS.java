package solutions.linked.slds;

import org.apache.clerezza.commons.rdf.IRI;

/**
 * The ontology as java constants
 */
public class SLDS {
    //core properties
    public static final IRI port = new IRI("https://vocab.linked.solutions/slds/port");
    public static final IRI resourceDescriptionProvider = new IRI("https://vocab.linked.solutions/slds/resourceDescriptionProvider");
    public static final IRI javaClass = new IRI("https://vocab.linked.solutions/slds/javaClass");
    //iri translation
    public static final IRI iriTranslators = new IRI("https://vocab.linked.solutions/slds/iriTranslators");
    public static final IRI backendPrefix = new IRI("https://vocab.linked.solutions/slds/backendPrefix");
    public static final IRI frontendPrefix = new IRI("https://vocab.linked.solutions/slds/frontendPrefix");
    //GraphAndContext provider
    public static final IRI sparqlEndpoint = new IRI("https://vocab.linked.solutions/slds/sparqlEndpoint");
    public static final IRI userName = new IRI("https://vocab.linked.solutions/slds/userName");
    public static final IRI password = new IRI("https://vocab.linked.solutions/slds/password");
    public static final IRI enableVituosoWorkAround = new IRI("https://vocab.linked.solutions/slds/enableVituosoWorkAround");
    public static final IRI disableResourceContext = new IRI("https://vocab.linked.solutions/slds/disableResourceContext");
    public static final IRI disableNamedGraph = new IRI("https://vocab.linked.solutions/slds/disableNamedGraph");
    //FileResourceDescription provider
    public static final IRI resource = new IRI("https://vocab.linked.solutions/slds/resource");
    public static final IRI file = new IRI("https://vocab.linked.solutions/slds/file");
    
    
}
