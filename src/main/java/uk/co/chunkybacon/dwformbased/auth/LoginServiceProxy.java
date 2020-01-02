package uk.co.chunkybacon.dwformbased.auth;

import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.server.UserIdentity;

import javax.servlet.ServletRequest;

public class LoginServiceProxy implements LoginService {

    private LoginService loginService;

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public String getName() {
        return loginService == null ? null : loginService.getName();
    }

    @Override
    public UserIdentity login(String s, Object o, ServletRequest servletRequest) {
        return loginService == null ? null : loginService.login(s, o, servletRequest);
    }

    @Override
    public boolean validate(UserIdentity userIdentity) {
        return loginService == null ? null : loginService.validate(userIdentity);
    }

    @Override
    public IdentityService getIdentityService() {
        return loginService == null ? null : loginService.getIdentityService();
    }

    @Override
    public void setIdentityService(IdentityService identityService) {
        if (this.loginService != null) {
            this.loginService.setIdentityService(identityService);
        }
    }

    @Override
    public void logout(UserIdentity userIdentity) {
        if (this.loginService != null) {
            this.loginService.logout(userIdentity);
        }
    }
}
