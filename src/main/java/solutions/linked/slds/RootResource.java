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
package solutions.linked.slds;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import org.apache.clerezza.api.utils.GraphNode;
import org.apache.clerezza.api.Graph;
import org.apache.clerezza.api.IRI;
import solutions.linked.slds.util.IriTranslatorProvider;

@Path("")
public abstract class RootResource {

    

    public final GraphNode config;
    private final IriTranslatorProvider iriTranslatorProvider;
    private final ConfigUtils configUtils;

    public RootResource(GraphNode config) {
        this.config = config;
        this.iriTranslatorProvider = new IriTranslatorProvider(config);
        this.configUtils = new ConfigUtils(config);
    }

    @GET
    @Path("{path : .*}")
    public Graph getResourceDescription(@Context HttpHeaders httpHeaders, @Context UriInfo uriInfo) throws IOException {
        final URI requestUri = uriInfo.getRequestUri();
        IRI resource = new IRI(requestUri.toString());
        return getGraphFor(resource);
    }

    
       
    /*protected GraphNode getGraphNodeFor(IRI resource) throws IOException {
        return new GraphNode(resource, getGraphFor(resource));
    }*/
    
    protected abstract Graph getGraphFor(IRI resource) throws IOException;

    
    
}
