package uk.co.chunkybacon.dwformbased;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class Config extends Configuration {

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swagger;

    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }

}
