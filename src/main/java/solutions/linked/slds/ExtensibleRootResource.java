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
package solutions.linked.slds;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ws.rs.Path;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.clerezza.rdf.utils.UnionGraph;

/**
 * The class looks for instances of xlds:ResourceDescriptionProvider in the
 * configuration and create the respective instances.
 * 
 * GET Requests to a resource are answered with the Union of the graphs 
 * returned by the providers.
 */
@Path("")
public class ExtensibleRootResource extends RootResource {
    
    final Collection<ResourceDescriptionProvider> resourceDescriptionProviders;
    
    public ExtensibleRootResource(GraphNode config) {
        super(config);
        Iterator<GraphNode> objectNodes = config.getObjectNodes(SLDS.resourceDescriptionProvider);
        resourceDescriptionProviders = new ArrayList<>();
        while (objectNodes.hasNext()) {
            GraphNode rdpNode = objectNodes.next();
            ResourceDescriptionProvider rdp = getRdpFromResource(rdpNode);
            resourceDescriptionProviders.add(rdp);
        }
        if (resourceDescriptionProviders.isEmpty()) {
            throw new RuntimeException("No ResourceDescriptionProvider found in config");
        }
    }

    @Override
    protected Graph getGraphFor(IRI effectiveResource) throws IOException {
        Collection<Graph> graphs = new ArrayList<>();
        for (ResourceDescriptionProvider rdp : resourceDescriptionProviders) {
            graphs.add(rdp.getGraphFor(effectiveResource));
        }
        return new UnionGraph(graphs.toArray(new Graph[graphs.size()]));
    }
    

    private ResourceDescriptionProvider getRdpFromResource(GraphNode rdpNode) {
        String className = rdpNode.getLiterals(SLDS.javaClass).next().getLexicalForm();
        try {
            return (ResourceDescriptionProvider) Class.forName(className).getConstructor(GraphNode.class).newInstance(rdpNode);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("ResourceDescriptionProvider "+className+" must have a public constructore with a GraphNode as argument", ex);
        } catch (SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } 
    }
    
}
