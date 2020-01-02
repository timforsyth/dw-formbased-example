package uk.co.chunkybacon.dwformbased.auth;

import org.eclipse.jetty.security.authentication.FormAuthenticator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import java.net.URI;

public class FormBasedAccessDeniedRedirector implements ExceptionMapper<ForbiddenException> {

    private final String loginUri;
    @Context
    private UriInfo ui;
    @Context
    private HttpServletRequest req;

    public FormBasedAccessDeniedRedirector(String loginUri) {
        this.loginUri = loginUri;
    }

    @Override
    public Response toResponse(ForbiddenException e) {
        String location = ui.getAbsolutePath().getPath();

        if (location != null) {
            req.getSession().setAttribute(FormAuthenticator.__J_URI, location);
        }
        return Response.temporaryRedirect(URI.create(this.loginUri)).build();
    }
}