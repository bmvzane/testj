package com.testj.resources.v1;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;

import com.google.inject.Inject;
import com.testj.api.users.UserResourceModel;
import com.testj.core.services.user.models.UserDTO;

import io.dropwizard.jersey.PATCH;

@Path("/api/v1/fake-users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FakeUserResource
{
    
    private final ModelMapper mapper;
    private static List<UserDTO> userCollection = new ArrayList<UserDTO>();
    
    @Inject
    public FakeUserResource(ModelMapper mapper)
    {
        this.mapper = mapper;
        
        if (userCollection.size() == 0) {
            DateTime dt = new DateTime("2017-01-01T23:59:59Z");
            
            for (long i=0; i<5; i++) {
                UserDTO userDTO    = new UserDTO();
                userDTO.id         = i;
                userDTO.email      = "test" + i + "@example.com";
                userDTO.name       = "test name " + i;
                userDTO.created_at = dt;
                
                userCollection.add(userDTO);
            }
        }
    }
    
    private long getMaxUserId()
    {
        return userCollection.stream()
                             .max(Comparator.comparing(u -> u.id))
                             .get()
                             .id;
    }
    
    @GET
    public Response getUsers()
    {
        return Response.ok(userCollection).build();
    }
    
    @POST
    public Response createUser(@Valid UserResourceModel userResourceModel, @Context UriInfo uriInfo)
    {
        long userId = this.getMaxUserId();
        userId++;
        
        UserDTO userDTO = this.mapper.map(userResourceModel, UserDTO.class);
        
        userDTO.id         = userId;
        userDTO.created_at = new DateTime();
        
        userCollection.add(userDTO);
        
        URI uri = uriInfo.getAbsolutePathBuilder()
                         .path(String.valueOf(userId))
                         .build();
        
        return Response.created(uri)
                       .entity(userDTO)
                       .build();
    }
    
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") long id)
    {
        UserDTO userDTO =  userCollection.stream()
                                         .filter(u -> u.id == id)
                                         .findFirst()
                                         .orElse(null);
        
        if (userDTO != null) {
            return Response.ok(userDTO).build();
        }
        
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @PATCH
    @Path("/{id}")
    public Response updateUser(@PathParam("id") long id, @Valid UserResourceModel userResourceModel)
    {
        UserDTO existingUser =  userCollection.stream()
                                              .filter(u -> u.id == id)
                                              .findFirst()
                                              .orElse(null);
        
        if (existingUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        if (userResourceModel.email != null) {
            existingUser.email = userResourceModel.email;
        }
        
        if (userResourceModel.name != null) {
            existingUser.name = userResourceModel.name;
        }
        
        userCollection = userCollection.stream()
                                       .map(o -> {
                                           return o.id == id ? existingUser : o;
                                       })
                                       .collect(Collectors.toList());
        
        return Response.ok(existingUser).build();
    }
    
}
