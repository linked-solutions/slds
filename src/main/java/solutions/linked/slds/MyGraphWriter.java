package solutions.linked.slds;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.jaxrs.rdf.providers.GraphWriter;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;

/**
 * This class is necessary, because
 * org.apache.clerezza.jaxrs.rdf.providers.GraphWriter expects to have a
 * serializer service injected
 */
@Provider
@Produces({
    SupportedFormat.RDF_XML, SupportedFormat.TURTLE,
    SupportedFormat.N3, SupportedFormat.N_TRIPLE, "application/ld+json"})
public class MyGraphWriter extends GraphWriter {

    public MyGraphWriter() {
        bindSerializer(Serializer.getInstance());
    }

    @Override
    public void writeTo(Graph t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            final OutputStream entityStream) throws IOException, WebApplicationException {

        OutputStream out = new OutputStream() {
            int count = 0;

            public void write(int b) throws IOException {
                entityStream.write(b);
                if ((++count % 4) == 0) {
                    //flushing very often prevents 
                    //java.io.IOException: Buffer overflow.
                    // at org.glassfish.jersey.netty.connector.internal.JerseyChunkedInput.write(JerseyChunkedInput.java:212
                    entityStream.flush();
                }
            }
        };
        super.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, out);
        out.flush();
    }

}
