package org.uab.authorisation.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.uab.user.service.UserServiceImpl;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/login")
public class LoginController {
    @Inject
    Template login;

    @Inject
    UserServiceImpl userServiceImpl;

    @Inject
    Template notification;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@Context SecurityContext securityContext) {
        if (securityContext.getUserPrincipal() != null) {
            return notification.data("message", "You are already logged in!", "role", userServiceImpl.getUserRole());
        }
        return login.data("role", userServiceImpl.getUserRole(), "message", null);
    }

    @GET
    @Path("/failed")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getOnFail() {
        String message = "Username or password incorrect, please try again!";
        return login.data("role", userServiceImpl.getUserRole(), "message", message);
    }
}
