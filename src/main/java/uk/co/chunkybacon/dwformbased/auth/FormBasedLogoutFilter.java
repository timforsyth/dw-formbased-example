package uk.co.chunkybacon.dwformbased.auth;

import io.dropwizard.jetty.setup.ServletEnvironment;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

public class FormBasedLogoutFilter implements Filter {

    private final String loginUri;
    private final String logoutUri;

    public FormBasedLogoutFilter(String loginUri, String logoutUri) {
        this.loginUri = loginUri;
        this.logoutUri = logoutUri;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
        ((HttpServletRequest)servletRequest).getSession().invalidate();
        ((HttpServletResponse)servletResponse).sendRedirect(this.loginUri);
    }

    @Override
    public void destroy() {
    }

    public void addToServlet(ServletEnvironment environment) {
        environment.addFilter("LogoutFilter", this)
            .addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST),
                false,
                this.logoutUri
            );
    }

}
