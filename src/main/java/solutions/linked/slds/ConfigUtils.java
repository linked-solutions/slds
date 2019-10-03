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

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.net.ssl.SSLContext;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.Literal;
import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * Provides some utility-methods based on configuration.
 *
 * @author noam
 */
public class ConfigUtils {

    final GraphNode config;
    PoolingHttpClientConnectionManager cm;

    public ConfigUtils(GraphNode config) {
        this.config = config;
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);

        
    }

    /**
     * Gets a CloseableHttpClient that authenticates using the credentials
     * supplied in the config.
     *
     * @return the HTTP Client
     */
    public CloseableHttpClient getHttpClient() {
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        addCredentials(credsProvider);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .build();
        return httpClient;
    }

    protected void addCredentials(final CredentialsProvider credsProvider) {
        if (getSparqlEndpointNode().getLiterals(SLDS.userName).hasNext()) {
            Credentials credentials = new UsernamePasswordCredentials(getUserName(), getPassword());
            credsProvider.setCredentials(AuthScope.ANY, credentials);
        }
    }

    public String getUserName() {
        return getSparqlEndpointNode().getLiterals(SLDS.userName).next().getLexicalForm();
    }

    public String getPassword() {
        return getSparqlEndpointNode().getLiterals(SLDS.password).next().getLexicalForm();
    }

    public GraphNode getSparqlEndpointNode() {
        try {
            return config.getObjectNodes(SLDS.sparqlEndpoint).next();
        } catch (NoSuchElementException ex) {
            throw new NoSuchElementException("the resource " + config.getNode() + " has no " + SLDS.sparqlEndpoint + " property.");
        }
    }

    public IRI getSparqlEndpointUri() {
        return (IRI) getSparqlEndpointNode().getNode();
    }

    public boolean enableVituosoWorkAround() {
        final Iterator<Literal> literals = config.getLiterals(SLDS.enableVituosoWorkAround);
        return (literals.hasNext() && LiteralFactory.getInstance().createObject(Boolean.class, literals.next()));
    }
}
