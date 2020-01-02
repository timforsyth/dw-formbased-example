package uk.co.chunkybacon.dwformbased.auth;

import io.dropwizard.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.security.LoginService;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.util.ArrayList;
import java.util.List;

public class FormBasedEnvironmentBinder<C extends Configuration> {

    private C config;
    private String protectAssetsAt;
    private List<String> whitelist = new ArrayList<>();
    private boolean redirectDropwizardAppToForm = true;

    public FormBasedEnvironmentBinder<C> using(final C config) {
        if (config.getServerFactory() instanceof FormBasedAuthenticationServerFactory) {
            this.config = config;
            return this;
        }
        throw new RuntimeException("Incorrect server type.\n\n" +
            "Make sure you have registered the FormBased server factory in " +
            "META-INF/services and you have set the server type to " +
            "form-based-authentication in your dropwizard config.");
    }

    public void bindTo(final Environment environment) {
        if (this.config == null) {
            throw new RuntimeException("Call using() first with configuration");
        }
        final FormBasedAuthenticationServerFactory factory = getServerFactory();
        final JerseyEnvironment jersey = environment.jersey();
        final ServletEnvironment servlets = environment.servlets();

        new FormBasedLogoutFilter(
            factory.loginUri(),
            factory.logoutUri()
        ).addToServlet(servlets);

        jersey.getResourceConfig().register(RolesAllowedDynamicFeature.class);

        if (this.protectAssetsAt != null) {
            final ProtectedUriFilter filter = new ProtectedUriFilter(
                factory.loginUri(),
                factory.loginErrorUri(),
                whitelist,
                factory.getJerseyRootPath().orElse(null),
                this.protectAssetsAt
            );
            filter.addToServlet(servlets);
        }

        if (this.redirectDropwizardAppToForm) {
            jersey.register(
                new FormBasedAccessDeniedRedirector(factory.loginUri())
            );
        }
    }

    public FormBasedEnvironmentBinder<C> protectAssetsAt(String path) {
        this.protectAssetsAt = path;
        return this;
    }

    public FormBasedEnvironmentBinder<C> redirectDropwizardAppToLogin(boolean redirectToForm) {
        this.redirectDropwizardAppToForm = redirectToForm;
        return this;
    }

    private FormBasedAuthenticationServerFactory getServerFactory() {
        return (FormBasedAuthenticationServerFactory)
            this.config.getServerFactory();
    }

    public FormBasedEnvironmentBinder<C> allow(String uri) {
        this.whitelist.add(uri);
        return this;
    }

    public FormBasedEnvironmentBinder<C> registerLoginService(LoginService loginService) {
        this.getServerFactory().setLoginService(loginService);
        return this;
    }
}
