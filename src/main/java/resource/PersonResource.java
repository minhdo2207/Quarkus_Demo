package resource;

import dto.request.PagingRequest;
import entity.Person;
import entity.common.QuerySearchResult;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import service.PersonService;
import utils.ApiUri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

@Path(ApiUri.PERSON)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Person")
@ApplicationScoped
public class PersonResource {
    @Inject
    PersonService personService;

    @GET
    public Uni<QuerySearchResult<Person>> getAll(@QueryParam(value = "page")
                                                 @DefaultValue("1") int page,
                                                 @QueryParam(value = "page_size") @DefaultValue("10") int pageSize,
                                                 @QueryParam(value = "keyword") String keyword,
                                                 @QueryParam(value = "status") @DefaultValue("-1") Long status,
                                                 @QueryParam(value = "sort_field") @DefaultValue("id") String sortField,
                                                 @QueryParam(value = "sort_by") @DefaultValue("DESC") String sortBy) {
        PagingRequest pagingRequest = new PagingRequest(page, pageSize, keyword, status, sortField, sortBy);
        return Uni.createFrom().converter(UniReactorConverters.fromMono(), personService.findAll(pagingRequest));
    }

    @GET
    @Path("{id}")
    public Uni<Response> get(@PathParam("id") Long id) {
        return personService.findById(id)
                .onItem()
                .transform(person -> person != null ? Response.ok(person) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(Person person) {
        return personService.create(person)
                .onItem()
                .transform(id -> {
                    try {
                        return Response.created(URI.create(URLEncoder.encode("/persons/" + id, "UTF-8"))).build();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    @PUT
    @Path("{id}")
    public Uni<Response> update(Person person, @PathParam("id") Long requestId) {
        return personService.update(person, requestId)
                .onItem()
                .transform(id -> {
                    try {
                        return Response.created(URI.create(URLEncoder.encode("/persons/" + id, "UTF-8"))).build();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return personService.delete(id)
                .onItem()
                .transform(deleted -> deleted != null ? Response.Status.OK : Response.Status.NOT_FOUND)
                .onItem()
                .transform(status -> Response.status(status).build());
    }

}
