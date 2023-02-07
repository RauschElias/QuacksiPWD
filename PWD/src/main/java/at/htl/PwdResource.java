package at.htl;

import at.htl.dtos.RequestUser;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/service")
public class PwdResource {

    @Inject
    PwdService service;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/login")
    public String login(RequestUser user) {
        return service.login(user.getUsername(), user.getPwd());
    }

    @POST
    @Path("/register")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String addNewUser(RequestUser user){
        return service.addNewUser(user.getUsername(),user.getPwd(),user.getEmail());
    }

    @GET
    @Path("/reset")
    @Produces(MediaType.TEXT_PLAIN)
    public String requestChangePWD(String username){
        if(service.userExists(username)){
            return "Username exixtiert nicht";
        }

        String token = service.requestPasswordChange(username);
        System.out.println("Email an " + service.getUser(username).getEmail() + "\nYour password reset token is: " + token);
        return "Ihnen wurde eine Email gesendet";
    }

    @PATCH
    @Path("/reset/{token}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String changePWD(@PathParam("token") String token, String pw){
        if (service.changePwd(token, pw)) {
            return "Password wurde geändert";
        }
        return "Unknown token";
    }
}