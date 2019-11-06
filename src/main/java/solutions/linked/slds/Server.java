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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import javax.ws.rs.ext.MessageBodyWriter;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.clerezza.rdf.utils.UnionGraph;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;

public class Server implements Runnable {


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Argument pointing to configuration required");
            return;
        }
        new Server(args).run();
    }

    public static GraphNode parseConfig(String[] path2config) throws FileNotFoundException {
        Graph configCraph = null;
        IRI configIRI = null; //this shall be the IRI of the first argument
        for (int i = 0; i < path2config.length; i++) {

            final File configFile = new File(path2config[i]);
            if (!configFile.exists()) {
                throw new FileNotFoundException("Could not find: " + configFile.getAbsolutePath());
            }
            //unfortunately this misses two slashes: configFile.toURI().toString();
            final String configFileURI = "file://" + configFile.toURI().normalize().toString().substring(5);
            final IRI currentFileIRI = new IRI(configFileURI);
            if (i == 0) {
                configIRI = currentFileIRI;
            }
            Graph currentFileConfig = Parser.getInstance().parse(new FileInputStream(configFile),
                    "text/turtle", currentFileIRI);
            configCraph = configCraph == null ? currentFileConfig : new UnionGraph(configCraph, currentFileConfig);
        }
        return new GraphNode(configIRI, configCraph);
    }
    
    public Server(String[] args) throws FileNotFoundException {
        this(Server.parseConfig(args));
    }
    
    public final GraphNode config;

    public Server(GraphNode config) {
        this.config = config;
    }

    @Override
    public void run() {
        int port = Integer.parseInt(config.getLiterals(SLDS.port).next().getLexicalForm());
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 1);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        HttpContextBuilder contextBuilder = new HttpContextBuilder();
        contextBuilder.getDeployment().setApplication(new Application() {
            @Override
            public Set<Object> getSingletons() {
                return getJaxRsComponents();
            }
        });
        contextBuilder.bind(server);
        server.start();
    }

    protected Set<Object> getJaxRsComponents() {
        Set<Object> result = new HashSet<>();
        result.add(getRootResource());
        result.add(getGraphMBW());
        result.add(new DummyWriter());
        result.add(new CORSFilter());
        result.add(new VaryFilter());
        result.add(new EffectiveRequestUriFilter());
        return result;
    }

    protected MessageBodyWriter getGraphMBW() {
        return new MyGraphWriter();
    }

    protected Object getRootResource() {
        return new ExtensibleRootResource(config);
    }

    /*protected ResourceDescriptionProvider getResourceDescriptionProvider() {
        return Resou
    }*/
}
