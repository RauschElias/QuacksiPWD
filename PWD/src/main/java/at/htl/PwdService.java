package at.htl;

import com.google.common.hash.Hashing;
import at.htl.entity.WebUser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class PwdService {

    // in memory list for password changes
    // as this simple structure doesn't require persisted password change tokens
    List<PasswordChange> passwordChanges = new LinkedList<>();

    public static final String saltAdditive = "_this_is_my_secure_salt_additive_no_one_is_ever_gonna_find_it_23p07640973609764=/&$=/&=/)$&";

    @Inject
    EntityManager manager;
    @Inject
    Validator validator;


    public boolean checkIfPasswordIsValid(WebUser user) {
        WebUser u = manager.createNamedQuery("getUserByName", WebUser.class).setParameter(1,user.getUserName()).getSingleResult();
        return hashPwd(user).equals(u.getPwd());
    }

    private String hashPwd(WebUser user) {
        return getHash(user.getPwSalt() + saltAdditive + user.getPwd());
    }

    public WebUser getUser(String name) {
        List<WebUser> u = manager.createNamedQuery("getUserByName", WebUser.class).setParameter(1,name).getResultList();
        return u.get(0);
    }

    private String  getHash(String hash) {
        return Hashing.sha256().hashString(hash,StandardCharsets.UTF_8).toString();
    }

    public String addNewUser(String username, String pwd, String email) {
        WebUser newUser = new WebUser();
        newUser.setUserName(username);
        Random rnd = new Random();
        byte[] rndBytes = new byte[16];
        rnd.nextBytes(rndBytes);
        newUser.setPwSalt(new String(Base64.getEncoder().encode(rndBytes)));
        newUser.setPwd(pwd);
        newUser.setPwd(hashPwd(newUser));
        newUser.setEmail(email);

        try {
            manager.persist(newUser);
        } catch (ConstraintViolationException e){
            return e.getMessage();
        }

        return newUser.getUserName() + " created";
    }

    public String requestPasswordChange(String username) {
        PasswordChange ch = new PasswordChange();
        List<WebUser> u =manager.createNamedQuery("getUserByName", WebUser.class).setParameter(1,username).getResultList();
        ch.user = u.get(0);
        ch.validUntil = LocalDateTime.now().plusMinutes(15);
        Random rnd = new Random();
        byte[] rndBytes = new byte[16];
        rnd.nextBytes(rndBytes);
        String token = new String(Base64.getEncoder().encode(rndBytes));
        ch.token = token;
        passwordChanges.add(ch);
        return token;
    }

    public boolean changePwd(String token, String pw) {
        for (PasswordChange ch: passwordChanges) {
            if (ch.token.equals(token)) {
                ch.user.setPwd(pw);
                ch.user.setPwd(hashPwd(ch.user));
                manager.persist(ch.user);
                return true;
            }
        }
        return false;
    }

    public boolean userExists(String username) {
        List<WebUser> u =manager.createNamedQuery("getUserByName", WebUser.class).setParameter(1,username).getResultList();
        return u.size() == 1;
    }

    public String login(String name, String password) {
        WebUser user = new WebUser();
        user.setUserName(name);
        user.setPwd(hashPwd(user));

        Result validationResult = validate(user);
        String errors = validationResult.getMessage();

        if (errors.isEmpty())
            return errors;
        else if (!userExists(name)){
            return "User does not exist";
        }
        else if(!checkIfPasswordIsValid(user)){
            return "Password is wrong";
        }

        return "logged in";
    }

    public Result validate(WebUser newUser){
        Set<ConstraintViolation<WebUser>> violations = validator.validate(newUser);
        if (violations.isEmpty()){
            return new Result("");
        }
        else{
            return new Result(violations);
        }
    }
}