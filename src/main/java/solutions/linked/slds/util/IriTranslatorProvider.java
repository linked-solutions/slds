/*
 * The MIT License
 *
 * Copyright 2017 FactsMission AG, Switzerland.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package solutions.linked.slds.util;

import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.utils.GraphNode;
import solutions.linked.slds.SLDS;
import solutions.linked.slds.translation.ChainedIriTranslator;
import solutions.linked.slds.translation.IriNamespaceTranslator;
import solutions.linked.slds.translation.IriTranslator;
import solutions.linked.slds.translation.NillIriTranslator;

/**
 * Provides an IriTranslator based on configuration.
 * 
 * @author noam
 */
public class IriTranslatorProvider {
    
    private final GraphNode config;
    
    public IriTranslatorProvider(GraphNode config) {
        this.config = config;
    }
    
    public IriTranslator getIriTranslator() {
        if (config.getObjectNodes(SLDS.iriTranslators).hasNext()) {
            final GraphNode next = config.getObjectNodes(SLDS.iriTranslators).next();
            return getIriTranslatorFromList(next);
        } else {
            return new NillIriTranslator();
        }
    }
    
    private IriTranslator getIriTranslatorFromList(GraphNode list) {
        if (list.getNode().equals(RDF.nil)) {
            return new NillIriTranslator();
        }
        return new ChainedIriTranslator(
                getIriTranslator(list.getObjectNodes(RDF.first).next()),
                getIriTranslatorFromList(list.getObjectNodes(RDF.rest).next()));
    }
    
    private IriTranslator getIriTranslator(GraphNode node) {
        return new IriNamespaceTranslator(node.getLiterals(SLDS.backendPrefix).next().getLexicalForm(), 
                    node.getLiterals(SLDS.frontendPrefix).next().getLexicalForm());
    }
}
