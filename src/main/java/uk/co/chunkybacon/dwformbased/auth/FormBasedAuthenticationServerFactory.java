package uk.co.chunkybacon.dwformbased.auth;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.MutableServletContextHandler;
import io.dropwizard.server.DefaultServerFactory;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.security.Constraint;

import javax.servlet.Servlet;
import javax.validation.Validator;

@JsonTypeName("form-based-authentication")
public class FormBasedAuthenticationServerFactory extends DefaultServerFactory {

    @JsonProperty
    private String loginUri;
    @JsonProperty
    private String loginErrorUri;
    @JsonProperty
    private String logoutUri;

    private LoginServiceProxy loginServiceProxy = new LoginServiceProxy();

    public String loginUri() {
        return loginUri;
    }

    public String loginErrorUri() {
        return loginErrorUri;
    }

    public String logoutUri() {
        return logoutUri;
    }

    @Override
    protected Handler createAppServlet(Server server, JerseyEnvironment jersey,
        ObjectMapper objectMapper, Validator validator,
        MutableServletContextHandler handler,Servlet jerseyContainer,
        MetricRegistry metricRegistry) {
        this.configureFormsAuthentication(handler);
        return super.createAppServlet(
            server,
            jersey,
            objectMapper,
            validator,
            handler,
            jerseyContainer,
            metricRegistry
        );
    }

    private void configureFormsAuthentication(MutableServletContextHandler ctx) {
        ctx.setSessionHandler(new SessionHandler());

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__FORM_AUTH);
        constraint.setRoles(new String[]{"user","admin","moderator"});
        constraint.setAuthenticate(true);

        FormAuthenticator authenticator = new FormAuthenticator(
            this.loginUri,
            this.loginErrorUri,
            false
        );

        ConstraintSecurityHandler handler = new ConstraintSecurityHandler();
        handler.setAuthenticator(authenticator);
        handler.setLoginService(this.loginServiceProxy);

        ctx.setSecurityHandler(handler);
    }

    public void setLoginService(LoginService loginService) {
        this.loginServiceProxy.setLoginService(loginService);
    }
}
