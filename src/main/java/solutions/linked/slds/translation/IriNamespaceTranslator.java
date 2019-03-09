package solutions.linked.slds.translation;

import org.apache.clerezza.api.utils.UriMutatingGraph;
import org.apache.clerezza.api.Graph;
import org.apache.clerezza.api.IRI;


public class IriNamespaceTranslator implements IriTranslator {
    
    final String origPrefix, targetPrefix;

    
    public IriNamespaceTranslator(String origPrefix, String targetPrefix) {
        this.origPrefix = origPrefix;
        this.targetPrefix = targetPrefix;
    }
    
    @Override
    public IriTranslator reverse() {
        return new IriNamespaceTranslator(targetPrefix, origPrefix);
    }
    
    @Override
    public IRI translate(IRI orig) {
       String origString = orig.getUnicodeString();
       if (origString.startsWith(origPrefix)) {
           return new IRI(targetPrefix+origString.substring(origPrefix.length()));
       } else {
           return orig;
       }
    }
    
    @Override
    public Graph translate(Graph orig) {
        return new UriMutatingGraph(orig, origPrefix, targetPrefix);
    }
    
}
