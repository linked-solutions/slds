package com.factsmission.tools.slds;

import io.netty.channel.Channel;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyWriter;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.impl.utils.simple.SimpleGraph;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.clerezza.rdf.utils.UnionGraph;
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider;
import org.glassfish.jersey.server.ResourceConfig;

public class Server implements Runnable{

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Argument pointing to configuration required");
            return;
        }
        Graph configCraph = null;
        IRI configIRI = null; //this shall be the IRI of the first argument
        for (int i = 0; i < args.length; i++) {

            final File configFile = new File(args[i]);
            //unfortunately this misses two slashes: configFile.toURI().toString();
            final String configFileURI = "file://"+configFile.toURI().normalize().toString().substring(5);
            final IRI currentFileIRI = new IRI(configFileURI);
            if (i == 0) { 
                configIRI = currentFileIRI;
            }
            Graph currentFileConfig = Parser.getInstance().parse(new FileInputStream(configFile), 
                    "text/turtle", currentFileIRI);
            configCraph = configCraph == null? currentFileConfig : new UnionGraph(configCraph, currentFileConfig);
        }
        new Server(new GraphNode(configIRI, configCraph)).run();
    }
    
    public final GraphNode config;

    public Server(GraphNode config) {
        this.config = config;
    }
    
    @Override
    public void run() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(5000).build();
        ResourceConfig jerseyConfig = new ResourceConfig();
        for (Object jaxRsComponent : getJaxRsComponents()) {
            jerseyConfig.register(jaxRsComponent);
        }
        Channel server = NettyHttpContainerProvider.createServer(baseUri, jerseyConfig, false);
    }
    
    protected Set<Object> getJaxRsComponents() {
        Set<Object> result = new HashSet<>();
        result.add(new ExceptionMapper());
        result.add(getRootResource());
        result.add(getGraphMBW());
        return result;
    }

    protected static MessageBodyWriter getGraphMBW() {
        return new MyGraphWriter();
    }

    protected Object getRootResource() {
        return new RootResource(config);
    }

}
