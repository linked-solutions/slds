package solutions.linked.slds.translation;

import org.apache.clerezza.api.Graph;
import org.apache.clerezza.api.IRI;

/**
 *
 * @author user
 */
public interface IriTranslator {

    IriTranslator reverse();

    IRI translate(IRI orig);

    Graph translate(Graph orig);
    
}
