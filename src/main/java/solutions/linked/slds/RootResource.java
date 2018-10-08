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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.clerezza.rdf.utils.UnionGraph;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import solutions.linked.slds.util.IriTranslatorProvider;

@Path("")
public class RootResource {

    

    private final GraphNode config;
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
    
    protected Graph getGraphFor(IRI resource) throws IOException {
        IRI effectiveResource = iriTranslatorProvider.getIriTranslator().reverse().translate(resource);
        return getGraphForTargetIri(effectiveResource);
    }

    protected Graph getGraphForTargetIri(IRI effectiveResource) throws IOException {
        final String[] queries = getQueries(effectiveResource);
        return runQueries(queries);
    }

    /**
     * The response of multiple queries are concatened so that the same b-node ID results in the same node
     */
    protected Graph runQueries(final String[] queries) throws IOException {
        InputStream in = new SequenceInputStream(getQueryResultsAsStream(queries));
        return iriTranslatorProvider.getIriTranslator().translate(Parser.getInstance()
                            .parse(in, "application/n-triples"));
    }
    protected Enumeration<? extends InputStream> getQueryResultsAsStream(final String[] queries) throws IOException {
        
        try (CloseableHttpClient httpClient = configUtils.createHttpClient()) {
            return Collections.enumeration(Arrays.asList(queries).stream().map(query -> {
                try {
                    final HttpPost httpPost = new HttpPost(configUtils.getSparqlEndpointUri().getUnicodeString());
                    if (configUtils.enableVituosoWorkAround()) {
                        //reason: https://github.com/openlink/virtuoso-opensource/issues/516
                       httpPost.setHeader("Accept", "text/plain");           
                    } else {
                       httpPost.setHeader("Accept", "application/n-triples");      
                    }
                    httpPost.setEntity(new StringEntity(query, ContentType.create("application/sparql-query", "utf-8")));
                    try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                        final StatusLine statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() >= 400) {
                            throw new IOException("HTTP "+statusLine.getStatusCode()
                                    +" "+statusLine.getReasonPhrase());
                        }
                        String responseType = response.getFirstHeader("Content-Type").getValue();
                        if (!(responseType.startsWith("application/n-triples") || (configUtils.enableVituosoWorkAround() && responseType.startsWith("text/plain")))) {
                            System.err.println("The SPARQL server did not retun n-triples but " + responseType);
                            System.err.println(new String(EntityUtils.toByteArray(response.getEntity()), "utf-8"));
                            throw new RuntimeException("The SPARQL server did not retun n-triples but " + responseType);
                        }
                        byte[] responseBody = EntityUtils.toByteArray(response.getEntity());
                        return (InputStream) new ByteArrayInputStream(responseBody);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
        }   
    }

    protected String[] getQueries(IRI resource) {
        return new String[]{
            "DESCRIBE <"+resource.getUnicodeString()+">",
            "CONSTRUCT {?sub ?pred ?obj} WHERE { GRAPH <"+resource.getUnicodeString()+"> {  ?sub ?pred ?obj . } }"
        };
    }
    
}
