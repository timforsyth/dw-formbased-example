package uk.co.chunkybacon.dwformbased;

import com.jayway.jsonassert.JsonAssert;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("unchecked")
public class AppTest {

    @ClassRule
    public static final DropwizardAppRule<Config> APP = new DropwizardAppRule<>(App.class, "./app.yml");

    @Test
    public void shouldRedirectToLoginPageForProtectedPath() {
        Response response = APP.client().target(relativeTo("/index.html"))
            .property(ClientProperties.FOLLOW_REDIRECTS, false)
            .request()
            .get();
        assertThat(response.getStatus(), is(302));
        assertThat(response.getHeaderString("location"), is(relativeTo("/login.html")));
    }

    @Test
    public void shouldRedirectToLoginPageForAPI() {
        Response response = APP.client().target(relativeTo("/api/fruit"))
            .property(ClientProperties.FOLLOW_REDIRECTS, false)
            .request()
            .get();
        assertThat(response.getStatus(), is(307));
        assertThat(response.getHeaderString("location"), is(relativeTo("/login.html")));
    }

    @Test
    public void shouldBeAbleToAccessWhitelistedLocations() {
        Response response = APP.client().target(relativeTo("/favicon.ico"))
            .property(ClientProperties.FOLLOW_REDIRECTS, false)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void shouldBeAbleToAccessTheIndexPageAfterLoggingIn() {
        String jsessionId = this.loginAndObtainSession();

        Response response = APP.client().target(relativeTo("/index.html"))
            .property(ClientProperties.FOLLOW_REDIRECTS, false)
            .request()
            .cookie("JSESSIONID", jsessionId)
            .get();
        assertThat(response.getStatus(), is(200));
        String html = response.readEntity(String.class);
        assertThat(html, containsString("Select a fruit to find out more"));
    }

    @Test
    public void shouldBeAbleToAccessTheAPIAfterLoggingIn() {
        String jsessionId = this.loginAndObtainSession();

        Response response = APP.client().target(relativeTo("/api/fruit"))
            .property(ClientProperties.FOLLOW_REDIRECTS, false)
            .request()
            .cookie("JSESSIONID", jsessionId)
            .get();
        assertThat(response.getStatus(), is(200));
        JsonAssert.with(response.readEntity(String.class))
            .assertThat("$.code", is(200))
            .assertThat("$.message", is("OK"))
            .assertThat("$.data", is(notNullValue()))
            .assertThat("$.data.total", is(4))
            .assertThat("$.data.items", is(JsonAssert.collectionWithSize(equalTo(4))));
    }

    private String loginAndObtainSession() {
        Response login = APP.client().target(relativeTo("/j_security_check"))
            .property(ClientProperties.FOLLOW_REDIRECTS, false)
            .request()
            .cookie("COOOKIE", "SDFSDF")
            .post(
                Entity.form(new Form()
                    .param("j_username", "user")
                    .param("j_password", "letmein")
                )
            );
        return login.getLocation().getPath().substring(
            login.getLocation().getPath().indexOf(";jsessionid=") + 12
        );
    }

    private String relativeTo(String path) {
        return String.format(
            "http://localhost:%s%s",
            APP.getLocalPort(),
            path
        );
    }
}
