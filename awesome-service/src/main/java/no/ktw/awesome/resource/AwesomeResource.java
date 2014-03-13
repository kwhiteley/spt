package no.ktw.awesome.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.Client;
import com.yammer.metrics.annotation.Timed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ktw
 */
@Path("/game")
@Produces(MediaType.APPLICATION_JSON)
public class AwesomeResource {
    
    private final Client client;

    public AwesomeResource(Client client) {
        this.client = client;
    }
    
    @GET
    @Timed
    public Response doSomething(@QueryParam("name") Optional<String> name) {
        return Response.ok(name.get()).build();
    }
    
}
