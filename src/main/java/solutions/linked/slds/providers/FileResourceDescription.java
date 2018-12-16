/*
 * The MIT License
 *
 * Copyright 2018 me.
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
package solutions.linked.slds.providers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.impl.utils.simple.SimpleGraph;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import solutions.linked.slds.ResourceDescriptionProvider;
import solutions.linked.slds.SLDS;
import solutions.linked.slds.translation.IriTranslator;
import solutions.linked.slds.util.IriTranslatorProvider;


public class FileResourceDescription implements ResourceDescriptionProvider {

    public final IRI resource;
    public final Graph graph;
    public final IriTranslator iriTranslator;
    
    public FileResourceDescription(GraphNode config) {
        this.iriTranslator = new IriTranslatorProvider(config).getIriTranslator();
        resource = iriTranslator.translate((IRI)config.getObjects(SLDS.resource).next());
        String filePath = config.getLiterals(SLDS.file).next().getLexicalForm();
        try {
            graph = iriTranslator.translate(Parser.getInstance().parse(new FileInputStream(filePath), "text/turtle"));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public Graph getGraphFor(IRI resource) {
        if (this.resource.equals(resource)) {
            return graph;
        } else {
            return new SimpleGraph();
        }
    }
    
}
