package com.testj.resources.v1;

import java.net.URI;
import java.util.List;

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

import org.modelmapper.ModelMapper;

import com.google.inject.Inject;
import com.testj.api.users.UserResourceModel;
import com.testj.core.services.user.UserService;
import com.testj.core.services.user.models.UserDTO;

import io.dropwizard.jersey.PATCH;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource
{
    
    private final ModelMapper mapper;
    private final UserService userService;

    @Inject
    public UserResource(
        ModelMapper mapper,
        UserService userService
    ) {
        this.mapper      = mapper;
        this.userService = userService;
    }
    
    @GET
    public Response getUsers()
    {
        List<UserDTO> users = this.userService.getUsers();
        
        return Response.ok(users).build();
    }
    
    @POST
    public Response createUser(@Valid UserResourceModel userResourceModel, @Context UriInfo uriInfo)
    {
        UserDTO userDTO = this.mapper.map(userResourceModel, UserDTO.class);
        
        userDTO = this.userService.createUser(userDTO);
        
        if (userDTO == null) {
            return Response.serverError().build();
        }
        
        URI uri = uriInfo
                  .getAbsolutePathBuilder()
                  .path(userDTO.id.toString())
                  .build();
        
        return Response.created(uri)
                       .entity(userDTO)
                       .build();
    }
    
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") long id)
    {
        UserDTO userDTO = this.userService.getUserById(id);
        
        if (userDTO != null) {
            return Response.ok(userDTO).build();
        }
        
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @PATCH
    @Path("/{id}")
    public Response updateUser(@PathParam("id") long id, @Valid UserResourceModel userResourceModel)
    {
        UserDTO existingUserDTO = this.userService.getUserById(id);
        
        if (existingUserDTO == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        UserDTO userDTO = this.mapper.map(userResourceModel, UserDTO.class);
        userDTO.id      = id;
        
        boolean success = this.userService.updateUser(userDTO);
        
        if (success) {
            UserDTO updatedUserDTO = this.userService.getUserById(id);
            
            return Response.ok(updatedUserDTO).build();
        }
        
        return Response.serverError().build();
    }
    
}
