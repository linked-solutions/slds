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
import java.net.URI;
import java.util.Arrays;
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
    public GraphNode getResourceDescription(@Context HttpHeaders httpHeaders, @Context UriInfo uriInfo) throws IOException {
        final URI requestUri = uriInfo.getRequestUri();
        //The server unfirtunately takes the hostname and port from its config
        //The following fixes this
        final String hostHeader = httpHeaders.getRequestHeader("Host").get(0);
        final int hostHeaderSeparator = hostHeader.indexOf(':');
        final String host = hostHeaderSeparator > -1 ? 
                hostHeader.substring(0, hostHeaderSeparator)
                : hostHeader;
        final int port  = hostHeaderSeparator > -1 ?
                Integer.parseInt(hostHeader.substring(hostHeaderSeparator+1))
                : -1;
        final URI fixedUri = UriBuilder.fromUri(requestUri).port(port).host(host).build();
        IRI resource = new IRI(fixedUri.toString());
        return getGraphNodeFor(resource);
    }

    
       
    protected GraphNode getGraphNodeFor(IRI resource) throws IOException {
        return new GraphNode(resource, getGraphFor(resource));
    }
    
    protected Graph getGraphFor(IRI resource) throws IOException {
        IRI effectiveResource = iriTranslatorProvider.getIriTranslator().reverse().translate(resource);
        return getGraphForTargetIri(effectiveResource);
    }

    protected Graph getGraphForTargetIri(IRI effectiveResource) throws IOException {
        final String[] queries = getQueries(effectiveResource);
        Graph[] graphs = Arrays.asList(queries).stream().map(q -> {
			try {
				return runQuery(q);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		})
                        .collect(Collectors.toList()).toArray(new Graph[queries.length]);
        UnionGraph result = new UnionGraph(graphs);
        return result;
    }

    protected Graph runQuery(final String query) throws IOException {
        try (CloseableHttpClient httpClient = configUtils.createHttpClient()) {
            final HttpPost httpPost = new HttpPost(configUtils.getSparqlEndpointUri().getUnicodeString());            
            System.out.println(query);
            httpPost.setEntity(new StringEntity(query, ContentType.create("application/sparql-query", "utf-8")));
            System.out.println(System.currentTimeMillis());
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                System.out.println(System.currentTimeMillis());
                final StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() >= 400) {
                    throw new IOException("HTTP "+statusLine.getStatusCode()
                            +" "+statusLine.getReasonPhrase());
                }
                byte[] responseBody = EntityUtils.toByteArray(response.getEntity());
                return iriTranslatorProvider.getIriTranslator().translate(Parser.getInstance()
                        .parse(new ByteArrayInputStream(responseBody),
                                response.getFirstHeader("Content-Type").getValue()));
            }
        }
    }

    protected String[] getQueries(IRI resource) {
        return new String[]{
            "DESCRIBE <"+resource.getUnicodeString()+">",
            "CONSTRUCT {?sub ?pred ?obj} WHERE { GRAPH <"+resource.getUnicodeString()+"> {  ?sub ?pred ?obj . } }"
        };
    }
    
}
