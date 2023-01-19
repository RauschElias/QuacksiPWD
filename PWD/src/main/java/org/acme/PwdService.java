package org.acme;

import com.google.common.hash.Hashing;
import org.acme.entity.User;
import org.acme.exceptions.UserFormatException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
public class PwdService {

    @Inject
    EntityManager manager;

    public boolean checkIfPasswordIsValid(User user) {
        User u =manager.createNamedQuery("getUserByName",User.class).setParameter(1,user.getUserName()).getSingleResult();
        return hashPwd(user) == u.getPwd();
    }

    private String hashPwd(User user) {
        StringBuilder reversedName = new StringBuilder(user.getUserName()).reverse();

        //hashing the reversed name
        String salt = Hashing.sha256().hashString(reversedName.toString(),StandardCharsets.UTF_8).toString();

        //hashing pwd plus the added salt
        return Hashing.sha256().hashString(salt+user.getPwd(), StandardCharsets.UTF_8).toString();
    }

    public String AddNewUser(String username, String pwd, String phoneNumber) {
        User newU = new User();
        newU.setUserName(username);
        newU.setPwd(hashPwd(newU));
        newU.setPhoneNumber(phoneNumber);

        try{
            newU.check();
        }catch (UserFormatException e){
            return e.getMessage();
        }

        manager.persist(newU);
        return newU.getUserName() + " created";
    }

    public void ChangePWD(String userName, String pwd) {
        manager.createNamedQuery("updatePwd").setParameter(1,pwd).setParameter(2,userName);
    }

    public boolean userExists(String username) {
        List<User> u =manager.createNamedQuery("getUserByName",User.class).setParameter(1,username).getResultList();
        return u.size() == 1;
    }

    public String login(String name, String password) {
        User newU = new User();
        newU.setUserName(name);
        newU.setPwd(hashPwd(newU));

        try{
            newU.check();
        }catch (UserFormatException e){
            return e.getMessage();
        }
        if (!userExists(name)){
            return "User does not exist";
        }
        else if(!checkIfPasswordIsValid(newU)){
            return "Password is wrong";
        }

        return "logged in";
    }
}