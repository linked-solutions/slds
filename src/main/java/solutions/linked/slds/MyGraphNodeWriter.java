package solutions.linked.slds;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.clerezza.jaxrs.rdf.providers.GraphNodeWriter;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;

/**
 * This class is necessary, because org.apache.clerezza.jaxrs.rdf.providers.GraphNodeWriter
 * expects to have a serializer service injected
 */
@Provider
@Produces({
    SupportedFormat.RDF_XML, SupportedFormat.TURTLE,
    SupportedFormat.N3, SupportedFormat.N_TRIPLE})
public class MyGraphNodeWriter extends GraphNodeWriter {

    public MyGraphNodeWriter() {
        bindSerializer(Serializer.getInstance());
    }
    
}
