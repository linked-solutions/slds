/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.factsmission.tools.slds;

import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.rdf.utils.UriMutatingGraph;


public class UriNamespaceTranslator {
    
    final String origPrefix, targetPrefix;

    
    public UriNamespaceTranslator(String origPrefix, String targetPrefix) {
        this.origPrefix = origPrefix;
        this.targetPrefix = targetPrefix;
    }
    
    public UriNamespaceTranslator reverse() {
        return new UriNamespaceTranslator(targetPrefix, origPrefix);
    }
    
    public IRI translate(IRI orig) {
       String origString = orig.getUnicodeString();
       if (origString.startsWith(origPrefix)) {
           return new IRI(targetPrefix+origString.substring(origPrefix.length()));
       } else {
           return orig;
       }
    }
    
    public Graph translate(Graph orig) {
        return new UriMutatingGraph(orig, origPrefix, targetPrefix);
    }
    
}
