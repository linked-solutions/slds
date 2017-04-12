package com.factsmission.tools.slds;


import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.impl.utils.TripleImpl;
import org.apache.clerezza.commons.rdf.impl.utils.TypedLiteralImpl;
import org.apache.clerezza.commons.rdf.impl.utils.simple.SimpleGraph;
import org.apache.clerezza.rdf.ontologies.DCTERMS;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.ontologies.XSD;
 
@Path("")
public class RootResource {
    
    final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
 
    @GET
    @Path("*")
    public Graph getResourceDescription(@Context UriInfo uriInfo)  {
      IRI resource = new IRI(uriInfo.getRequestUri().toString());
        final Graph result = new SimpleGraph();
        
            result.add(new TripleImpl(resource, 
                    DCTERMS.date, 
                    new TypedLiteralImpl(dateFormat.format(new Date()), XSD.date)));
            
        return result;
    }
    
    
    @GET
    @Path("debug")
    public String debug() throws ParseException, IOException {
        return "hello";
    }
    
    
}