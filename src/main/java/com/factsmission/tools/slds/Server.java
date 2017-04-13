package com.factsmission.tools.slds;

import io.netty.channel.Channel;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyWriter;
import org.apache.clerezza.rdf.utils.graphnodeprovider.GraphNodeProvider;
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider;
import org.glassfish.jersey.server.ResourceConfig;

public class Server implements Runnable{

    public static void main(String[] args) throws Exception {
        new Server().run();
    }

    public Server() {
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
        result.add(getRootResource());
        result.add(getGraphMBW());
        return result;
    }

    protected static MessageBodyWriter getGraphMBW() {
        return new MyGraphWriter();
    }

    protected Object getRootResource() {
        return new RootResource();
    }

}
