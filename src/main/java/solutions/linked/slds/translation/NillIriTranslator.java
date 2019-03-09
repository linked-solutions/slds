package solutions.linked.slds.translation;

import org.apache.clerezza.api.Graph;
import org.apache.clerezza.api.IRI;

public class NillIriTranslator implements IriTranslator {

    public NillIriTranslator() {
    }

    @Override
    public IriTranslator reverse() {
        return this;
    }

    @Override
    public IRI translate(IRI orig) {
        return orig;
    }

    @Override
    public Graph translate(Graph orig) {
        return orig;
    }
    
}
