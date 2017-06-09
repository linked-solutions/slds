package solutions.linked.slds.translation;

import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;

/**
 *
 * @author user
 */
public interface IriTranslator {

    IriTranslator reverse();

    IRI translate(IRI orig);

    Graph translate(Graph orig);
    
}
