package uk.co.chunkybacon.dwformbased;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import uk.co.chunkybacon.dwformbased.auth.FormBasedEnvironmentBinder;
import uk.co.chunkybacon.dwformbased.auth.SimpleLoginService;
import uk.co.chunkybacon.dwformbased.resource.ApiResource;

public class App extends Application<Config> {

    public static void main(String... args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(Config config) {
                return config.getSwagger();
            }
        });
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html", "assets"));
    }

    @Override
    public void run(Config config, Environment environment) throws Exception {
        FormBasedEnvironmentBinder<Config> formBasedBinder = new FormBasedEnvironmentBinder<>();
        formBasedBinder
            .using(config)
            .protectAssetsAt("/")
            .allow("/css")
            .allow("/favicon.ico")
            .registerLoginService(
                new SimpleLoginService()
                    .addUser("user", "letmein", "admin")
                )
            .bindTo(environment);

        environment.jersey().register(new ApiResource());
    }
}
