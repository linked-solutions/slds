package com.factsmission.tools.slds;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import javax.net.ssl.SSLContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.Literal;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

@Path("")
public class RootResource {

    private final GraphNode config;

    RootResource(GraphNode config) {
        this.config = config;
    }

    @GET
    @Path("{path : .*}")
    public Graph getResourceDescription(@Context UriInfo uriInfo) throws IOException {
        IRI resource = new IRI(uriInfo.getRequestUri().toString());
        return getGraphFor(resource);
    }

    protected CloseableHttpClient createClient() {
        try {
            final HttpClientBuilder hcb = HttpClientBuilder.create();
            final CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("public", "public"));
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
            hcb.setSSLContext(sslContext);
            return hcb.setDefaultCredentialsProvider(credsProvider).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    @GET
    @Path("debug")
    public Object debug() throws ParseException, IOException {
        return System.getProperties();
    }

    protected String getSparqlEndpoint() {
        return ((Literal)config.getObjects(SLDS.sparqlEndpoint).next()).getLexicalForm();
    }
    
    protected UriNamespaceTranslator getUriNameSpaceTranslator() {
        return new UriNamespaceTranslator("http://treatment.plazi.org/", 
                "http://localhost:5000/");
    }

    protected Graph getGraphFor(IRI resource) throws IOException {
        IRI effectiveResource = getUriNameSpaceTranslator().reverse().translate(resource);
        try (CloseableHttpClient httpClient = createClient()) {
            final HttpPost httpPost = new HttpPost(getSparqlEndpoint());
            final String query = "DESCRIBE <"+effectiveResource.getUnicodeString()+">";
            System.out.println(query);
            httpPost.setEntity(new StringEntity(query, ContentType.create("application/sparql-query", "utf-8")));
            System.out.println(System.currentTimeMillis());
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                System.out.println(System.currentTimeMillis());
                if (response.getStatusLine().getStatusCode() >= 400) {
                    throw new IOException(response.getStatusLine().getReasonPhrase());
                }
                byte[] responseBody = EntityUtils.toByteArray(response.getEntity());
                return getUriNameSpaceTranslator().translate(Parser.getInstance()
                        .parse(new ByteArrayInputStream(responseBody),
                        response.getFirstHeader("Content-Type").getValue()));
            }
        }
    }

    

}
