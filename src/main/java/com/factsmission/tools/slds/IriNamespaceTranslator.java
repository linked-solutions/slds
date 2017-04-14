package com.factsmission.tools.slds;

import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.rdf.utils.UriMutatingGraph;


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
