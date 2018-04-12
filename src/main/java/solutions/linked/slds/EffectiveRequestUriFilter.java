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

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriBuilder;

/**
 * Unfortuntaley jersey takes the hostname and port from its config,
 * this filter ensures resources get the hostname and port as per the 
 * HTTP headers
 */
@PreMatching
public class EffectiveRequestUriFilter implements ContainerRequestFilter {

    
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
        final URI requestUri = requestContext.getUriInfo().getRequestUri();
        final String hostHeader = requestContext.getHeaders().getFirst("Host");
        final int hostHeaderSeparator = hostHeader.indexOf(':');
        final String host = hostHeaderSeparator > -1 ? 
                hostHeader.substring(0, hostHeaderSeparator)
                : hostHeader;
        final int port  = hostHeaderSeparator > -1 ?
                Integer.parseInt(hostHeader.substring(hostHeaderSeparator+1))
                : -1;
        final String xForwardedProto = requestContext.getHeaders().getFirst("X-Forwarded-Proto");
        final UriBuilder uriBuilder;
        if (xForwardedProto != null) {
            uriBuilder = UriBuilder.fromUri(requestUri).scheme(xForwardedProto);
        } else {
            uriBuilder = UriBuilder.fromUri(requestUri);
        }
        final URI fixedUri = uriBuilder.port(port).host(host).build();
        requestContext.setRequestUri(fixedUri);
	}

}