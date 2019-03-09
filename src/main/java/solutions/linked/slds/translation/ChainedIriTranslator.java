package solutions.linked.slds.translation;

import org.apache.clerezza.api.Graph;
import org.apache.clerezza.api.IRI;

public class ChainedIriTranslator implements IriTranslator {
    
    final IriTranslator first, second;

    public ChainedIriTranslator(IriTranslator first, IriTranslator second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public IriTranslator reverse() {
        return new ChainedIriTranslator(second.reverse(), first.reverse());
    }

    @Override
    public IRI translate(IRI orig) {
        return second.translate(first.translate(orig));
    }

    @Override
    public Graph translate(Graph orig) {
        return second.translate(first.translate(orig));
    }
    
}
