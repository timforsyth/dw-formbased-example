package uk.co.chunkybacon.dwformbased.resource;

import com.jayway.jsonassert.JsonAssert;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApiResourceTest {

    private ResourceTestRule api;

    @Rule
    public TestRule setup() {
        this.api = new ResourceTestRule.Builder().addResource(new ApiResource()).build();
        return this.api;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnAListOfFruit() {
        Response response = api.target("/fruit").request().get();
        assertThat(response.getStatus(), is(200));

        JsonAssert.with(response.readEntity(String.class))
            .assertThat("$.code", is(200))
            .assertThat("$.message", is("OK"))
            .assertThat("$.data", is(notNullValue()))
            .assertThat("$.data.total", is(4))
            .assertThat("$.data.items", is(JsonAssert.collectionWithSize(equalTo(4))))
            .assertThat("$.data.items[0].name", is("Banana"))
            .assertThat("$.data.items[0].colour", is("Yellow"))
            .assertThat("$.data.items[0].edibleSkin", is(false))
            .assertThat("$.data.items[1].name", is("Blueberry"))
            .assertThat("$.data.items[1].colour", is("Dark Blue"))
            .assertThat("$.data.items[1].edibleSkin", is(true))
            .assertThat("$.data.items[2].name", is("Kiwi"))
            .assertThat("$.data.items[2].colour", is("Green"))
            .assertThat("$.data.items[2].edibleSkin", is(false))
            .assertThat("$.data.items[3].name", is("Raspberry"))
            .assertThat("$.data.items[3].colour", is("Red"))
            .assertThat("$.data.items[3].edibleSkin", is(true));
    }

    @Test
    public void shouldReturnASingleFruitByName() {
        Response response = api.target("/fruit/Blueberry").request().get();
        assertThat(response.getStatus(), is(200));

        JsonAssert.with(response.readEntity(String.class))
            .assertThat("$.code", is(200))
            .assertThat("$.message", is("OK"))
            .assertThat("$.data", is(notNullValue()))
            .assertThat("$.data.name", is("Blueberry"))
            .assertThat("$.data.colour", is("Dark Blue"))
            .assertThat("$.data.edibleSkin", is(true));
    }

    @Test
    public void shouldReturnA404ForSingleFruitWhenNotFound() {
        Response response = api.target("/fruit/Cloudberry").request().get();
        assertThat(response.getStatus(), is(404));

        JsonAssert.with(response.readEntity(String.class))
            .assertThat("$.code", is(404))
            .assertThat("$.message", is("NOT FOUND"));
    }
}
