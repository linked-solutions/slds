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
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider;
import org.glassfish.jersey.server.ResourceConfig;

public class Server implements Runnable{

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Argument pointing to configuration required");
        }
        final File configFile = new File(args[0]);
        //unfortunately this misses two slashes: configFile.toURI().toString();
        final String configFileURI = "file://"+configFile.toURI().toString().substring(5);
        final IRI configIRI = new IRI(configFileURI);
        Graph configCraph = Parser.getInstance().parse(new FileInputStream(configFile), 
                "text/turtle", configIRI);
        new Server(new GraphNode(configIRI, configCraph)).run();
    }
    
    public final GraphNode config;

    public Server(GraphNode config) {
        this.config = config;
    }
    
    @Override
    public void run() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(5000).build();
        ResourceConfig config = new ResourceConfig();
        for (Object jaxRsComponent : getJaxRsComponents()) {
            config.register(jaxRsComponent);
        }
        Channel server = NettyHttpContainerProvider.createServer(baseUri, config, false);
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
