package org.acme;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class PwdResource {

    @Inject
    PwdService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String login(final String name,final String pwd) {
        return service.login(name, pwd);
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String addNewUser(String username,String pwd,String pwdRepeat,String phoneNumber){
        return service.AddNewUser(username,pwd,phoneNumber);
    }

    @PATCH
    @Produces(MediaType.TEXT_PLAIN)
    public String changePWD(String userName,String pwd,String pwdRepeat){
        if(pwd != pwdRepeat)
            return "Passwords are not matching";
        else if(service.userExists(userName)){
            return "username exixtiert nicht";
        }

        service.ChangePWD(userName,pwd);
        return "Passwort wurde zurückgesetzt";
    }
}