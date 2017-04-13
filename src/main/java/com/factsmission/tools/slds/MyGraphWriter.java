package com.factsmission.tools.slds;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.clerezza.jaxrs.rdf.providers.GraphWriter;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;

/**
 *
 * @author user
 */
@Provider
@Produces({
    SupportedFormat.RDF_XML, SupportedFormat.TURTLE,
    SupportedFormat.N3, SupportedFormat.N_TRIPLE})
public class MyGraphWriter extends GraphWriter {

    public MyGraphWriter() {
        bindSerializer(Serializer.getInstance());
    }
    
}
