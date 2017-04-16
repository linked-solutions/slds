package com.factsmission.tools.slds.translation;

import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;

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
