package uk.co.chunkybacon.dwformbased.auth;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.util.security.Password;

public class SimpleLoginService extends HashLoginService {

    private final UserStore userStore;

    public SimpleLoginService() {
        this.userStore = new UserStore();
        setUserStore(this.userStore);
    }

    public SimpleLoginService addUser(String username, String password, String... roles) {
        this.userStore.addUser(username, new Password(password), roles);
        return this;
    }
}
