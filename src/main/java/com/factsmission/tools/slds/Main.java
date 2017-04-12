package com.factsmission.tools.slds;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
 
public class Main {
    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
 
        Server jettyServer = new Server(5000);
        
        EnumSet<DispatcherType> all = EnumSet.of(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD,
            DispatcherType.INCLUDE, DispatcherType.REQUEST);      
        FilterHolder jerseyFilter = context.addFilter(org.glassfish.jersey.servlet.ServletContainer.class, "/*", all);
        jerseyFilter.setInitParameter(
           "jersey.config.server.provider.classnames",
           RootResource.class.getCanonicalName()+", "+
           MyGraphWriter.class.getCanonicalName());
        jerseyFilter.setInitParameter(
           "jersey.config.servlet.filter.forwardOn404",
           "true");
        Resource resourceRoot = Resource.newResource(Main.class.getResource("/META-INF/resources/"));
        context.setBaseResource(resourceRoot);
        
        ServletHolder holderPwd = new ServletHolder("default",DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed","true");
        context.addServlet(holderPwd,"/");
        jettyServer.setHandler(context);
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}