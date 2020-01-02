package uk.co.chunkybacon.dwformbased.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import uk.co.chunkybacon.dwformbased.model.Fruit;
import uk.co.chunkybacon.dwformbased.model.Greengrocer;
import uk.co.chunkybacon.dwformbased.resource.representation.ApiResponse;
import uk.co.chunkybacon.dwformbased.resource.representation.ApiResponse.CollectionBuilder;
import uk.co.chunkybacon.dwformbased.resource.representation.ApiResponse.DataCollection;
import uk.co.chunkybacon.dwformbased.resource.representation.ApiResponse.SingleItemBuilder;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@RolesAllowed("admin")
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value="Example API")
public class ApiResource {

    private final List<Fruit> fruits = Arrays.asList(
        Greengrocer.banana(),
        Greengrocer.blueberry(),
        Greengrocer.kiwi(),
        Greengrocer.raspberry()
    );

    @ApiOperation(value = "Example Fruit API", notes="This API requires 'admin' role permissions")
    @ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "", response = ApiResponse.class)
    })

    @GET
    @Path("fruit")
    public ApiResponse<DataCollection<Fruit>> allFruit() {
        return new CollectionBuilder<Fruit>()
            .status(200)
            .message("OK")
            .items(this.fruits)
            .build();
    }

    @GET
    @Path("/fruit/{name}")
    public Response fruitByName(@PathParam("name") String name) {
        return this.fruits.stream()
            .filter(f -> f.name().equals(name))
            .findFirst()
            .map(f -> new SingleItemBuilder<Fruit>()
                .status(200)
                .message("OK")
                .data(f)
                .build()
            ).orElseGet(() -> new SingleItemBuilder<Fruit>()
                .status(404)
                .message("NOT FOUND")
                .build()
            ).asResponse();
    }
}



