package uk.co.chunkybacon.dwformbased.auth;

import io.dropwizard.jetty.setup.ServletEnvironment;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class ProtectedUriFilter implements Filter {

    private final String loginUri;
    private final String loginErrorUri;
    private final List<String> whitelist;
    private final String rootContextPath;
    private final String protectedPath;

    public ProtectedUriFilter(String loginUri, String loginErrorUri, List<String> whitelist, String rootContextPath, String protectedPath) {
        this.loginUri = loginUri;
        this.loginErrorUri = loginErrorUri;
        this.whitelist = whitelist;
        this.rootContextPath = rootContextPath;
        this.protectedPath = protectedPath;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        if (userIsNotPresent(servletRequest) &&
            pathIsNotDropwizardResource(uri) &&
            isNotLoginPage(uri) &&
            isNotWhitelisted(uri)) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.sendRedirect(this.loginUri);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean userIsNotPresent(ServletRequest servletRequest) {
        return ((HttpServletRequest) servletRequest).getUserPrincipal() == null;
    }

    private boolean pathIsNotDropwizardResource(String uri) {
        return (rootContextPath == null && !"/".equals(uri)) ||
            (rootContextPath != null && !uri.startsWith(rootContextPath));
    }

    private boolean isNotLoginPage(String uri) {
        return !uri.equals(this.loginUri) &&
            !uri.equals(this.loginErrorUri);
    }

    private boolean isNotWhitelisted(String uri) {
        return whitelist.stream().noneMatch(uri::startsWith);
    }

    @Override
    public void destroy() {
    }

    void addToServlet(final ServletEnvironment environment) {
        environment.addFilter("ProtectedUriFilter", this)
            .addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST),
                true,
                String.format(
                    "%s%s*",
                    this.protectedPath,
                    this.protectedPath.endsWith("/") ? "": "/"
                )
            );
    }
}
