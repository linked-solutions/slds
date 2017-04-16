/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.factsmission.tools.slds.translation;

import com.factsmission.tools.slds.translation.IriTranslator;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;

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
