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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import solutions.linked.slds.ConfigUtils;
import solutions.linked.slds.ResourceDescriptionProvider;
import solutions.linked.slds.util.IriTranslatorProvider;


public class GraphAndContext implements ResourceDescriptionProvider {

    public final GraphNode config;
    public final IriTranslatorProvider iriTranslatorProvider;
    public final ConfigUtils configUtils;
    
    public GraphAndContext(GraphNode config) {
        this.config = config;
        configUtils = new ConfigUtils(config);
        this.iriTranslatorProvider = new IriTranslatorProvider(config);
    }
    
    @Override
    public Graph getGraphFor(IRI resource) {
        IRI effectiveResource = iriTranslatorProvider.getIriTranslator().reverse().translate(resource);
        final String[] queries = getQueries(effectiveResource);
        try {
            return runQueries(queries);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * The response of multiple queries are concatenated so that the same b-node ID results in the same node
     */
    protected Graph runQueries(final String[] queries) throws IOException {
        InputStream in = new SequenceInputStream(getQueryResultsAsStream(queries));
        return iriTranslatorProvider.getIriTranslator().translate(Parser.getInstance()
                            .parse(in, "application/n-triples"));
    }
    protected Enumeration<? extends InputStream> getQueryResultsAsStream(final String[] queries) throws IOException {
        
        CloseableHttpClient httpClient = configUtils.getHttpClient();
        {
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
        String describeQuery = "DESCRIBE <"+resource.getUnicodeString()+">";
        Set<String> resultSet = new HashSet<String>();
        if (!configUtils.disableResourceContext()) {
            if (configUtils.enableVituosoWorkAround()) {
                resultSet.add("define sql:describe-mode \"SCBD\" "+describeQuery);
            } else { 
                resultSet.add(describeQuery);
            }
        }
        if (!configUtils.disableNamedGraph()) {
            resultSet.add("CONSTRUCT {?sub ?pred ?obj} WHERE { GRAPH <"+resource.getUnicodeString()+"> {  ?sub ?pred ?obj . } }");
        }
        return resultSet.toArray(new String[resultSet.size()]);
    }
    
}
