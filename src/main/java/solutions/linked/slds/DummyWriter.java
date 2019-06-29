package solutions.linked.slds;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.annotation.Priority;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.clerezza.commons.rdf.Graph;


/** 
 * This writer is to prevent the default rendering. It just returns an HTTP 406 
 * response
 */

@Provider
@Produces({"*/*; qs=0.01"})
@Priority(Integer.MAX_VALUE)
public class DummyWriter implements MessageBodyWriter<Graph> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, 
            Annotation[] annotations, MediaType mediaType) {
        return Graph.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(Graph t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
    }




}
