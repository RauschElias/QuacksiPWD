package org.acme;

import com.google.common.hash.Hashing;
import org.acme.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class PwdService {

    @Inject
    EntityManager manager;
    @Inject
    Validator validator;


    public boolean checkIfPasswordIsValid(User user) {
        User u =manager.createNamedQuery("getUserByName",User.class).setParameter(1,user.getUserName()).getSingleResult();
        return hashPwd(user).equals(u.getPwd());
    }

    private String hashPwd(User user) {
        StringBuilder reversedName = new StringBuilder(user.getUserName()).reverse();

        //hashing the reversed name
        String salt = Hashing.sha256().hashString(reversedName.toString(),StandardCharsets.UTF_8).toString();

        //hashing pwd plus the added salt
        return Hashing.sha256().hashString(salt+user.getPwd(), StandardCharsets.UTF_8).toString();
    }

    private String  getHash(String hash){
        return Hashing.sha256().hashString(hash,StandardCharsets.UTF_8).toString();
    }

    public String AddNewUser(String username, String pwd, String phoneNumber) {
        User newUser = new User();
        newUser.setUserName(username);
        newUser.setPwd(hashPwd(newUser));
        newUser.setPhoneNumber(phoneNumber);

        try {
            manager.persist(newUser);
        }catch (ConstraintViolationException e){
            return e.getMessage();
        }

        return newUser.getUserName() + "created";
    }

    public String ChangePWD(String userName, String pwd) {
        if (!userExists(userName))
            return "user does not exist";

        try {
            manager.createNamedQuery("updatePwd").setParameter(1,pwd).setParameter(2,userName);
        }catch (ConstraintViolationException e){
            return e.getMessage();
        }
        return "changed password";
    }

    public boolean userExists(String username) {
        List<User> u =manager.createNamedQuery("getUserByName",User.class).setParameter(1,username).getResultList();
        return u.size() == 1;
    }

    public String login(String name, String password) {
        User user = new User();
        user.setUserName(name);
        user.setPwd(hashPwd(user));

        Result validationResult = Validate(user);
        String errors = validationResult.getMessage();

        if (errors.isEmpty())
            return errors;
        else if (!userExists(name)){
            return "User does not exist";
        }
        else if(!checkIfPasswordIsValid(user)){
            return "Password is wrong";
        }

        return "loged in";
    }

    public Result Validate(User newUser){
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        if (violations.isEmpty()){
            return new Result("");
        }
        else{
            return new Result(violations);
        }
    }
}