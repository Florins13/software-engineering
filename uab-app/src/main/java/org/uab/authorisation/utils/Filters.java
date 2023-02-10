package org.uab.authorisation.utils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;


@Provider
public class Filters implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseContext.getHeaders().add("Pragma", "no-cache");
        responseContext.getHeaders().add("Expires", "0");
    }
}
